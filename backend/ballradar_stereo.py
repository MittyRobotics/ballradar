import utils.v4l2_set as v4l2_set
from utils.trackers import CentroidTracker, StereoTracker
from utils import ntclient
from utils.math import doMath, angleToBall, getDepthFromRobotBase
from utils.torch_utils import select_device
from utils.general import (check_img_size, non_max_suppression, scale_coords)
from utils.datasets import LoadStreams
from models.common import DetectMultiBackend
import os
import sys
from pathlib import Path
import numpy as np

import cv2
import torch
import torch.backends.cudnn as cudnn

FILE = Path(__file__).resolve()
ROOT = FILE.parents[0]  # YOLOv5 root directory
if str(ROOT) not in sys.path:
    sys.path.append(str(ROOT))  # add ROOT to PATH
ROOT = Path(os.path.relpath(ROOT, Path.cwd()))  # relative


data = ROOT / 'data/coco128.yaml'
dnn = False  # use OpenCV DNN for ONNX inference
source = "0"
conf_thres = 0.8  # confidence threshold
iou_thres = 0.45  # NMS IOU threshold
max_det = 1000  # maximum detections per image
classes = None  # filter by class: --class 0, or --class 0 2 3
agnostic_nms = False  # class-agnostic NMS
augment = False  # augmented inference
visualize = False  # visualize features
line_thickness = 3  # bounding box thickness (pixels)
view_img = True

B = 20.32         #Distance between the cameras [cm]
focal = 24.81072      #Camera lense's focal length [mm]
alpha = 52        #Camera field of view in the horisontal plane [degrees]


trackers = [CentroidTracker(), CentroidTracker(idStartingPoint=500)]
stereoTracker = StereoTracker()

with open("streams.txt") as f:
    sources = [x.strip()
               for x in f.read().strip().splitlines() if len(x.strip())]

def yolo(path, im, im0s, device, names, model, dataset):
    im = torch.from_numpy(im).to(device)
    im = im.float()
    im /= 255
    if len(im.shape) == 3:
        im = im[None]

    pred = model(im, augment=augment, visualize=visualize)
    pred = non_max_suppression(
        pred, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)

    rects = []

    for i, det in enumerate(pred):

        p, im0, _ = path[i], im0s[i].copy(), dataset.count
        p = Path(p)

        if det is not None and len(det):
            det[:, :4] = scale_coords(
                im.shape[2:], det[:, :4], im0.shape).round()

            for *xyxy, conf, cls in reversed(det):
                rects.append(((int(xyxy[0]), int(xyxy[1]), int(
                    xyxy[2]), int(xyxy[3])), names[int(cls)], conf, i))

    return rects

def find_depth(right_point, left_point, frame_right, frame_left, baseline, focal, alpha):
    
    # CONVERT FOCAL LENGTH f FROM [mm] TO [pixel]:
    height_right, width_right, depth_right = frame_right.shape
    height_left, width_left, depth_left = frame_left.shape

    if width_right == width_left:
        f_pixel = (focal / (640 * 0.084)) * width_right

    else:
        print('Left and right camera frames do not have the same pixel width')

    x_right = right_point[0]
    x_left = left_point[0]

    # CALCULATE THE DISPARITY:
    disparity = x_left-x_right      #Displacement between left and right frames [pixels]

    # CALCULATE DEPTH z:
    zDepth = (baseline*f_pixel)/disparity             #Depth in [cm]

    return zDepth


@torch.no_grad()
def run():

    ntclient.set_vision_connected(False)

    # Load model
    imgsz = [320, 320]  # inference size (height, width)

    device = select_device('0')
    model = DetectMultiBackend(
        'runs/train/exp/weights/ball.pt', device=device, dnn=dnn, data=data)
    stride, names, pt = model.stride, model.names, model.pt
    imgsz = check_img_size(imgsz, s=stride)

    # v4l2_set.setCameraProps()

    # Dataloader
    cudnn.benchmark = True  # set True to speed up constant image size inference
    dataset = LoadStreams("streams.txt", img_size=imgsz,
                          stride=stride, auto=pt, stereo=True)

    frameSkip = 10
    frameCount = 0

    ballString = ""

    model.warmup(imgsz=(1, 3, *imgsz))
    for i, (path, im, im0s, vid_cap, s) in enumerate(dataset):

        ntclient.set_vision_connected(True)

        frameCount += 1

        ntclient.clear()

        if not ntclient.check_connected():
           ntclient.table = ntclient.waitForConnection()
        

        if frameCount % frameSkip == 0:
            frameCount = 0
            ballString = ""
            raw_rects = []
            raw_rects = yolo(path, im, im0s, device, names, model, dataset)

            split_raw_rects = {}

            for rect in raw_rects:
                cam_id = rect[3]
                if cam_id not in split_raw_rects:
                    split_raw_rects[cam_id] = []

                split_raw_rects[cam_id].append(rect)

            for key in split_raw_rects.keys():
                trackers[key].update(split_raw_rects[key])

            stereoTracker.update(trackers[0], trackers[1])

        for (left_cam_ball, right_cam_ball) in stereoTracker.get_pairs():
            
            # right point, left point, right cam, left cam
            zDepth = find_depth(right_cam_ball[1][0], left_cam_ball[1][0], im0s[1], im0s[0], B, focal, alpha)
            zDepth = round(zDepth, 2)
            angle_x, angle_y = angleToBall(left_cam_ball[1])
            zDepth = getDepthFromRobotBase(zDepth, angle_y)

            if zDepth is not None and angle_x is not None:
                ballString += str(left_cam_ball[0]) + "," + str(zDepth) + "," + str(angle_x) + "," + str(left_cam_ball[1][1][1]) + str(left_cam_ball[1][1][2]) + "," + str(left_cam_ball[1][1][3]) + " "

            if type(zDepth) is str:
                cv2.putText(im0s[0], zDepth, (left_cam_ball[0], left_cam_ball[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)
                cv2.putText(im0s[1], zDepth, (right_cam_ball[0], right_cam_ball[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)


        # 1 tracker per camera
        for j in range(2):
            for i, (oid, centroid) in enumerate(trackers[j].get_data().items()):
                xyxy = centroid[1][0]
                color = centroid[1][1]
                conf = centroid[1][2]
                cam = centroid[1][3]

                label = f'ID: {oid}, {color}, {conf:.2f}'
                cv2.rectangle(im0s[cam], (xyxy[0], xyxy[1]),
                              (xyxy[2], xyxy[3]), (0, 255, 0), 2)
                cv2.putText(im0s[cam], label, (xyxy[0], xyxy[1] - 10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

        ntclient.add_ball(ballString)


        cv2.imshow("ball radar", im0s[0])
        cv2.imshow("ball radar 2", im0s[1])


if __name__ == "__main__":
    run()
