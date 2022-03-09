import cv2
import math

ball_cascade = cv2.CascadeClassifier('runs/train/exp/weights/ball_cascade.xml')

# cap = cv2.VideoCapture(0)

# time.sleep(2.0)

COLORS = (
    (0, 0, 255),
    (255, 0, 0),
)

def closest_color(rgb):
    r, g, b = rgb
    color_diffs = []
    for color in COLORS:
        cr, cg, cb = color
        color_diff = math.sqrt((r - cr)**2 + (g - cg)**2 + (b - cb)**2)
        color_diffs.append((color_diff, color))
    return min(color_diffs)[1]

def draw_border(img, pt1, pt2, color, thickness, r, d):
    x1, y1 = pt1
    x2, y2 = pt2

    cv2.line(img, (x1 + r, y1), (x1 + r + d, y1), color, thickness)
    cv2.line(img, (x1, y1 + r), (x1, y1 + r + d), color, thickness)
    cv2.ellipse(img, (x1 + r, y1 + r), (r, r), 180, 0, 90, color, thickness)

    cv2.line(img, (x2 - r, y1), (x2 - r - d, y1), color, thickness)
    cv2.line(img, (x2, y1 + r), (x2, y1 + r + d), color, thickness)
    cv2.ellipse(img, (x2 - r, y1 + r), (r, r), 270, 0, 90, color, thickness)

    cv2.line(img, (x1 + r, y2), (x1 + r + d, y2), color, thickness)
    cv2.line(img, (x1, y2 - r), (x1, y2 - r - d), color, thickness)
    cv2.ellipse(img, (x1 + r, y2 - r), (r, r), 90, 0, 90, color, thickness)

    cv2.line(img, (x2 - r, y2), (x2 - r - d, y2), color, thickness)
    cv2.line(img, (x2, y2 - r), (x2, y2 - r - d), color, thickness)
    cv2.ellipse(img, (x2 - r, y2 - r), (r, r), 0, 0, 90, color, thickness)

def cascadeDetection(frame, croppedInfo=None):
    frame_gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    faces = ball_cascade.detectMultiScale(frame_gray, 1.2, 3, minSize=(24, 24))

    rects = []

    for (x, y, w, h) in faces:
        # draw_border(frame, (x, y), (x + w, y + h), (255, 0, 105), 4, 15, 10)

        center = (int(x + w/2), int(y + h/2))
        color = frame[center[1], center[0]]
        matched_color = "blueball" if closest_color((color[2], color[1], color[0])) == (0, 0, 255) else "redball"

        x1 = int(x)
        y1 = int(y)

        x2 = int(x + w)
        y2 = int(y + h)

        if croppedInfo is not None:
            x1 = int(x1 + 20 + croppedInfo[0])
            y1 = int(y1 - 20 + croppedInfo[1])
            x2 = int(x2 + 20 + croppedInfo[0])
            y2 = int(y2 - 20 + croppedInfo[1])
        
        rects.append(((x1, y1, x2, y2), matched_color, 1))

    return rects

# while(cap.isOpened()):

#     ret, frame = cap.read()

#     rects = cascadeDetection(frame)


#     cv2.imshow('frame', frame)
#     if cv2.waitKey(1) & 0xFF == ord('q'):
#         break

# cap.release()

# cv2.destroyAllWindows()
