import cv2
import numpy as np
import imutils
import time


def sobelEdge(frame):
    
    scale = 1
    delta = 0
    ddepth = cv2.CV_16S

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    blur = cv2.GaussianBlur(gray, (5, 5), 0)


    # grad_x = cv2.Sobel(gray, ddepth, 1, 0, ksize=3, scale=scale, delta=delta, borderType=cv2.BORDER_DEFAULT)
    # grad_y = cv2.Sobel(gray, ddepth, 0, 1, ksize=3, scale=scale, delta=delta, borderType=cv2.BORDER_DEFAULT)

    # abs_grad_x = cv2.convertScaleAbs(grad_x)
    # abs_grad_y = cv2.convertScaleAbs(grad_y)

    # grad = cv2.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0)

    mid = cv2.Canny(blur, 30, 150)

    # edges = cv2.Canny(image=frame, threshold1=50, threshold2=200)

    cv2.imshow("Mid Edge Map", mid)

    # return edges


cap = cv2.VideoCapture(0)
time.sleep(2.0)


while(cap.isOpened()):

	ret, frame = cap.read()

	sobel = sobelEdge(frame);


	# cv2.imshow('frame', sobel)
	if cv2.waitKey(1) & 0xFF == ord('q'):
    		break

cap.release()
cv2.destroyAllWindows()