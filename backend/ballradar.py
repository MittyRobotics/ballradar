import os
import sys
import time
from pathlib import Path

import cv2
import torch
import torch.backends.cudnn as cudnn

FILE = Path(__file__).resolve()
ROOT = FILE.parents[0]  # YOLOv5 root directory
if str(ROOT) not in sys.path:
    sys.path.append(str(ROOT))  # add ROOT to PATH
ROOT = Path(os.path.relpath(ROOT, Path.cwd()))  # relative

from models.common import DetectMultiBackend
from utils.datasets import LoadStreams
from utils.general import (check_img_size, non_max_suppression, scale_coords)
from utils.torch_utils import select_device
from utils.math import doMath
# from utils import ntclient
from utils.centroidcalculator import CentroidTracker
from utils.contour import contourDetection
from utils.cascade import cascadeDetection
from utils.augmentations import letterbox
import utils.v4l2_set as v4l2_set
import numpy as np

        
data=ROOT / 'data/coco128.yaml'
half=False  # use FP16 half-precision inference
dnn=False  # use OpenCV DNN for ONNX inference
source = "0"
conf_thres=0.8  # confidence threshold
iou_thres=0.45  # NMS IOU threshold
max_det=1000  # maximum detections per image
classes=None  # filter by class: --class 0, or --class 0 2 3
agnostic_nms=False  # class-agnostic NMS
augment=False  # augmented inference
visualize=False  # visualize features
line_thickness=3  # bounding box thickness (pixels)
view_img = True

ct = CentroidTracker()

def yolo(path, im, im0s, device, names, model, dataset):
    im = torch.from_numpy(im).to(device)
    im = im.half() if half else im.float()
    im /= 255
    if len(im.shape) == 3:
        im = im[None] 

    pred = model(im, augment=augment, visualize=visualize)
    pred = non_max_suppression(pred, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)

    rects = []

    for i, det in enumerate(pred):

        p, im0, _ = path[i], im0s[i].copy(), dataset.count
        p = Path(p)


        if det is not None and len(det):
            det[:, :4] = scale_coords(im.shape[2:], det[:, :4], im0.shape).round()

            for *xyxy, conf, cls in reversed(det):
                rects.append(((int(xyxy[0]), int(xyxy[1]), int(xyxy[2]), int(xyxy[3])), names[int(cls)], conf, i))

    return rects

@torch.no_grad()
def run():

    # Load model
    device = ''  # cuda device, i.e. 0 or 0,1,2,3 or cpu
    imgsz=[320, 320]  # inference size (height, width)

    device = select_device(device)
    model = DetectMultiBackend('runs/train/exp/weights/ball.pt', device=device, dnn=dnn, data=data)
    stride, names, pt = model.stride, model.names, model.pt
    imgsz = check_img_size(imgsz, s=stride)

    # v4l2_set.setCameraProps()

    # Dataloader
    cudnn.benchmark = True  # set True to speed up constant image size inference
    dataset = LoadStreams("streams.txt", img_size=imgsz, stride=stride, auto=pt)

    frameSkip = 10
    frameCount = 0

    ballString = ""
    rects = []
    objects = {}

    model.warmup(imgsz=(1, 3, *imgsz), half=half)
    for i, (path, im, im0s, vid_cap, s) in enumerate(dataset):

        first_img = cv2.resize(im0s[0], (320, 240), interpolation=cv2.INTER_LINEAR)
        second_img = cv2.resize(im0s[1], (320, 240), interpolation=cv2.INTER_LINEAR)

        concat = cv2.vconcat([first_img, second_img])


        # cv2.imshow("concat", concat)

        frameCount += 1
        # print("frame: ", frameCount)

        fromBulkFunction = False
        
        # ntclient.clear()

        # if not ntclient.check_connected():
            # ntclient.table = ntclient.waitForConnection()

        # OPTIONS:
        # CASCADE DETECTION, CONTOUR DETECTION

        if frameCount % frameSkip == 0:
            frameCount = 0
            ballString = ""
            rects = []
            objects = {}
            rects = yolo(path, im, im0s, device, names, model, dataset)
            objects = ct.update(rects, fromBulkFunction)
        else:
            # rectsTemp = contourDetection(im0s[0])
            # for rect in rectsTemp:
            #     (top_left_x, top_left_y, btm_right_x, btm_right_y) = rect[0]
            #     cropped_img = im0s[0][top_left_y - 20:btm_right_y + 20, top_left_x - 20:btm_right_x + 20]
            #     if 0 in cropped_img.shape:
            #         continue
            #     rects.extend(cascadeDetection(cropped_img, croppedInfo=rect[0]))

            # rects = contourDetection(im0s[0])

            fromBulkFunction = True

        # objects = ct.update(rects, fromBulkFunction)

        for i, (oid, centroid) in enumerate(objects.items()):
            xyxy = centroid[1][0]
            color = centroid[1][1]
            conf = centroid[1][2]
            cam = centroid[1][3]

            label = f'ID: {oid}, {color}, {conf:.2f}' if not fromBulkFunction else f'ID: {oid}, {color}'
            cv2.rectangle(im0s[cam], (xyxy[0], xyxy[1]), (xyxy[2], xyxy[3]), (0, 255, 0), 2)
            cv2.putText(im0s[cam], label, (xyxy[0], xyxy[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

            ballString += doMath(xyxy, oid, color, conf)
        

        # ntclient.add_ball(ballString)

        cv2.imshow("ball radar", im0s[0])
        cv2.imshow("ball radar 2", im0s[1])
        print(rects)
        




if __name__ == "__main__":
    run()
