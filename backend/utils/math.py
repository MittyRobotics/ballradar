import math
import numpy as np

# height of camera in meters
cam_height = 0.75

def doMath(xyxy, ballid, ballcolor, conf):

    # image resolution
    resolution = (640, 480)
    # center of image
    center = (resolution[0]/2, resolution[1]/2)

    # bounding box of detected ball
    bounding_top_left_x = (int(xyxy[0])) - center[0]
    bounding_top_left_y = (int(xyxy[1])) - center[1]
    bounding_btm_right_x = (int(xyxy[2])) - center[0]
    bounding_btm_right_y = (int(xyxy[3])) - center[1]

    # bounding width and height
    bounding_width = bounding_btm_right_x - bounding_top_left_x
    bounding_height = bounding_btm_right_y - bounding_top_left_y

    # center of bounding box
    center_pixel = ((bounding_top_left_x + bounding_btm_right_x) / 2, (bounding_top_left_y + bounding_btm_right_y) / 2)

    # horizontal & vertical fields of view
    hfov = 52
    vfov = 39

    # angle to center pixel in X and Y directions
    angle_x = (center_pixel[0] / resolution[0]) * hfov
    angle_y = (center_pixel[1] / resolution[1]) * vfov
    
    # error checking (if bounding box is 0, dividng by 0)
    if bounding_width > 0 and bounding_height > 0 and np.sin(angle_x * np.pi/180) != 0 and np.sin(angle_y * np.pi/180) != 0:

        distance = 0

        clipped_x = False
        clipped_y = False

        # if ball clipped into left edge, use height for distance
        if bounding_top_left_x - 2 <= -(resolution[0] / 2):
            distance = (center_pixel[1] / np.sin(angle_y * np.pi/180)) * (9.5 / bounding_height)
            clipped_x = True
        # if ball is clipped into right edge, use height for distance
        elif bounding_btm_right_x + 2 >= (resolution[0] / 2):
            distance = (center_pixel[1] / np.sin(angle_y * np.pi/180)) * (9.5 / bounding_height)
            clipped_x = True
        # if ball is clipped into bottom edge, use width for distance
        if bounding_top_left_y - 2 <= -(resolution[1] / 2):
            distance = (center_pixel[0] / np.sin(angle_x * np.pi/180)) * (9.5 / bounding_width)
            clipped_y = True
        # if ball is clipped into top edge, use width for distance
        elif bounding_btm_right_y + 2 >= (resolution[1] / 2):
            distance = (center_pixel[0] / np.sin(angle_x * np.pi/180)) * (9.5 / bounding_width)
            clipped_y = True

        # if ball is in a corner, ignore
        if clipped_x and clipped_y:
            return ""

        # if ball is anywhere else, average height and width
        if not clipped_x and not clipped_y:
            distance_x = (center_pixel[0] / np.sin(angle_x * np.pi/180)) * (9.5 / bounding_width)
            distance_y = (center_pixel[1] / np.sin(angle_y * np.pi/180)) * (9.5 / bounding_height)

            distance = (distance_x + distance_y) / 2

        # convert to meters
        distance *= 0.0254

        ground_distance = 0

        # calculate ground distance using pythagorean theorem
        if(distance**2 - cam_height**2 >= 0):
            ground_distance = math.sqrt(distance**2 - cam_height**2)
        else:
            ground_distance = distance
            

        # return string with ball data          
        return str(ballid) + "," + str(ground_distance) + "," + str(angle_x) + "," + ballcolor + f",{conf:.2f} "

    # ball not detected, return nothing
    return ""