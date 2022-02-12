import os
import sys
from pathlib import Path

os.environ["OMP_NUM_THREADS"] = "1"
os.environ["OPENBLAS_NUM_THREADS"] = "1"
os.environ["MKL_NUM_THREADS"] = "1"
os.environ["VECLIB_MAXIMUM_THREADS"] = "1"
os.environ["NUMEXPR_NUM_THREADS"] = "1"

import cv2
import torch
import shutil
import torch.backends.cudnn as cudnn

FILE = Path(__file__).resolve()
ROOT = FILE.parents[0]  # YOLOv5 root directory
if str(ROOT) not in sys.path:
    sys.path.append(str(ROOT))  # add ROOT to PATH
ROOT = Path(os.path.relpath(ROOT, Path.cwd()))  # relative

from models.common import DetectMultiBackend
from utils.datasets import LoadStreams
from utils.general import (check_img_size, non_max_suppression, scale_coords, xyxy2xywh)
from utils.plots import Annotator, colors
from utils.torch_utils import select_device, time_sync
from utils.math import doMath
# from utils import ntclient
from deep_sort.utils.parser import get_config
from deep_sort.deep_sort import DeepSort

@torch.no_grad()
def run():
        
    data=ROOT / 'data/coco128.yaml'
    imgsz=(640, 640)  # inference size (height, width)
    conf_thres=0.25  # confidence threshold
    iou_thres=0.45  # NMS IOU threshold
    max_det=1000  # maximum detections per image
    device=''  # cuda device, i.e. 0 or 0,1,2,3 or cpu
    classes=None  # filter by class: --class 0, or --class 0 2 3
    agnostic_nms=False  # class-agnostic NMS
    augment=False  # augmented inference
    visualize=False  # visualize features
    line_thickness=3  # bounding box thickness (pixels)
    hide_labels=False  # hide labels
    hide_conf=False  # hide confidences
    half=False  # use FP16 half-precision inference
    dnn=False  # use OpenCV DNN for ONNX inference
    source = "0"

    # Load model
    device = select_device(device)

    cfg = get_config()
    cfg.merge_from_file("deep_sort/configs/deep_sort.yaml")
    deepsort = DeepSort("osnet_x0_25",
        device,
        max_dist=cfg.DEEPSORT.MAX_DIST,
        max_iou_distance=cfg.DEEPSORT.MAX_IOU_DISTANCE,
        max_age=cfg.DEEPSORT.MAX_AGE, n_init=cfg.DEEPSORT.N_INIT, nn_budget=cfg.DEEPSORT.NN_BUDGET,
        )

    out = "inference/output"
    evaluate = False
    
    half &= device.type != 'cpu'
    if not evaluate:
        if os.path.exists(out):
            pass
            shutil.rmtree(out)  # delete output folder
    os.makedirs(out)  # make new output folder


    model = DetectMultiBackend('runs/train/exp/weights/best.pt', device=device, dnn=dnn, data=data)
    stride, names, pt = model.stride, model.names, model.pt
    imgsz = check_img_size(imgsz, s=stride)

    # Dataloader
    view_img = True
    cudnn.benchmark = True  # set True to speed up constant image size inference
    dataset = LoadStreams(source, img_size=imgsz, stride=stride, auto=pt)
    bs = len(dataset)
    names = model.module.names if hasattr(model, 'module') else model.names


    model.warmup(imgsz=(1, 3, *imgsz), half=half)
    dt, seen = [0.0, 0.0, 0.0, 0.0], 0
    for frame_idx, (path, im, im0s, vid_cap, s) in enumerate(dataset):
        t1 = time_sync()
        im = torch.from_numpy(im).to(device)
        im = im.half() if half else im.float()
        im /= 255
        if len(im.shape) == 3:
            im = im[None] 
        t2 = time_sync()
        dt[0] += t2 - t1

        pred = model(im, augment=augment, visualize=visualize)
        t3 = time_sync()
        dt[1] += t3 - t2

        pred = non_max_suppression(pred, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)
        dt[2] += time_sync() - t3

        for i, det in enumerate(pred):
            seen += 1
            p, im0, _ = path[i], im0s[i].copy(), dataset.count
            s += f'{i}: '

            p = Path(p)

            annotator = Annotator(im0, line_width=line_thickness, example=str(names))
            # ntclient.clear()

            if det is not None and len(det):
                det[:, :4] = scale_coords(im.shape[2:], det[:, :4], im0.shape).round()

                xywhs = xyxy2xywh(det[:, 0:4])
                confs = det[:, 4]
                clss = det[:, 5]

                ballString = ""

                t4 = time_sync()
                outputs = deepsort.update(xywhs.cpu(), confs.cpu(), clss.cpu(), im0)
                t5 = time_sync()
                dt[3] += t5 - t4

                if len(outputs) > 0:
                    for j, (output, conf) in enumerate(zip(outputs, confs)):

                        bboxes = output[0:4]
                        id = output[4]
                        cls = output[5]

                        c = int(cls)  # integer class
                        label = f'{id} {names[c]} {conf:.2f}'
                        annotator.box_label(bboxes, label, color=colors(c, True))


                # for *xyxy, conf, cls in reversed(det):
                #     c = int(cls)
                #     label = None if hide_labels else (names[c] if hide_conf else f'{names[c]} {conf:.2f}')
                #     annotator.box_label(xyxy, label, color=colors(c, True))

                #     ballString += doMath(xyxy, label)

                # ntclient.add_ball(ballString)
            else:
                deepsort.increment_ages()
            
            # if not ntclient.check_connected():
            #     ntclient.table = ntclient.waitForConnection()

            im0 = annotator.result()
            if view_img:
                cv2.imshow(str(p), im0)
                cv2.waitKey(1) 


if __name__ == "__main__":
    run()
