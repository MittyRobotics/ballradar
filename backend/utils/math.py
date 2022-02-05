from dis import dis
import numpy as np
from networktables import NetworkTables

NetworkTables.initialize(server='127.0.0.1')
print("initialized networktables")
table = NetworkTables.getTable('ballradar')
print("found ball radar table")

def doMath(xyxy, label):

    resolution = (640, 480)
    center = (resolution[0]/2, resolution[1]/2)

    bounding_btm_left_x = (int(xyxy[0])) - center[0]
    bounding_btm_left_y = (int(xyxy[1])) - center[1]
    bounding_top_right_x = (int(xyxy[2])) - center[0]
    bounding_top_right_y = (int(xyxy[3])) - center[1]

    bounding_width = bounding_top_right_x - bounding_btm_left_x
    bounding_height = bounding_top_right_y - bounding_btm_left_y

    center_pixel = ((bounding_btm_left_x + bounding_top_right_x) / 2, (bounding_btm_left_y + bounding_top_right_y) / 2)

    hfov = 52
    vfov = 39

    angle_x = (center_pixel[0] / resolution[0]) * hfov
    angle_y = (center_pixel[1] / resolution[1]) * vfov
    
    if bounding_width > 0 and bounding_height > 0 and np.sin(angle_x * np.pi/180) != 0 and np.sin(angle_y * np.pi/180) != 0:

        distance_x = (center_pixel[0] / np.sin(angle_x * np.pi/180)) * (9.5 / bounding_width)
        distance_y = (center_pixel[1] / np.sin(angle_y * np.pi/180)) * (9.5 / bounding_height)

        distance = (distance_x + distance_y) / 2
        distance *= 0.0254

        table.putString('ball', str(distance) + " " + str(angle_x) + " " + label)