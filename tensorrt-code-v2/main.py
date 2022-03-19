
from cvu.detector import Detector
import cv2

cap = cv2.VideoCapture(0)

# create detector
detector = Detector(classes=["redball", "blueball"], weight="ball.pt", backend="tensorrt", device="cuda")

while True:
    ret, frame = cap.read()

    pred = detector(frame)

    pred.draw(frame)

    cv2.imshow("frame", frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
