import cv2
import numpy as np
import imutils
import time

lowerBoundBlue = (86, 116, 52)
upperBoundBlue = (132, 195, 228)

lowerBoundRed = (154, 144, 75)
upperBoundRed = (189, 224, 198)

lowerBounds = [lowerBoundBlue, lowerBoundRed]
upperBounds = [upperBoundBlue, upperBoundRed]


def contourDetection(frame):

    rects = []
    	
    blurred = cv2.GaussianBlur(frame, (11, 11), 0)
    hsv = cv2.cvtColor(blurred, cv2.COLOR_BGR2HSV)

    for i in range(2):

        mask = cv2.inRange(hsv, lowerBounds[i], upperBounds[i])
        mask = cv2.erode(mask, None, iterations=2)
        mask = cv2.dilate(mask, None, iterations=2)

        contours = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        contours = imutils.grab_contours(contours)
        center = None

        # if len(contours) > 0:
        for c in contours:
            ((x, y), radius) = cv2.minEnclosingCircle(c)
            M = cv2.moments(c)
            center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))

            if radius > 15:
                # cv2.circle(frame, (int(x), int(y)), int(radius), (0, 255, 255), 2)
                # cv2.circle(frame, center, 5, (0, 0, 255), -1)

                bounding_top_left_x = int(center[0] - radius)
                bounding_top_left_y = int(center[1] - radius)
                bounding_bottom_right_x = int(center[0] + radius)
                bounding_bottom_right_y = int(center[1] + radius)

                rects.append(((bounding_top_left_x, bounding_top_left_y, bounding_bottom_right_x, bounding_bottom_right_y), "blueball" if i == 0 else "redball", 1))
    
    return rects

# cap = cv2.VideoCapture(0)
# time.sleep(2.0)



# while(cap.isOpened()):
    
#     ret, frame = cap.read()
#     rects = contourDetection(frame);
#     print(rects)
    
#     cv2.imshow('frame', frame)
#     if cv2.waitKey(1) & 0xFF == ord('q'):
#     	break

# cap.release()

# cv2.destroyAllWindows()
