import cv2
import numpy as np
import imutils
import time

def circleDetection(gray, original):

    output = original.copy()

    # gray = cv2.cvtColor(output, cv2.COLOR_BGR2GRAY)
    blur = cv2.medianBlur(gray, 7)

    circles = cv2.HoughCircles(blur, cv2.HOUGH_GRADIENT, 1.5, 200)

    if circles is not None:
        circles = np.round(circles[0, :]).astype("int")
        for (x, y, r) in circles:
            cv2.circle(output, (x, y), r, (0, 255, 0), 4)
            cv2.rectangle(output, (x - 5, y - 5), (x + 5, y + 5), (0, 128, 255), -1)
            
    return output

def sobelEdge(frame):
    
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    # gray = cv2.GaussianBlur(gray, (3, 3), 0)

    ksize = 3
    gX = cv2.Sobel(gray, ddepth=cv2.CV_32F, dx=1, dy=0, ksize=ksize)
    gY = cv2.Sobel(gray, ddepth=cv2.CV_32F, dx=0, dy=1, ksize=ksize)

    gX = cv2.convertScaleAbs(gX)
    gY = cv2.convertScaleAbs(gY)

    grad = cv2.addWeighted(gX, 0.5, gY, 0.5, 0)
    
    return grad

cap = cv2.VideoCapture(0)
time.sleep(2.0)


while(cap.isOpened()):
    
    ret, frame = cap.read()
    
    sobel = sobelEdge(frame)
    circles = circleDetection(sobel, frame)
    # cv2.imshow("Hough", circles)
    cv2.imshow("Sobel + Hough", circles)
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
    	break

cap.release()
cv2.destroyAllWindows()