import sys
import numpy as np
import time
import imutils
import cv2

cv_file = cv2.FileStorage()
cv_file.open('stereoMap.xml', cv2.FileStorage_READ)

stereoMapL_x = cv_file.getNode('stereoMapL_x').mat()
stereoMapL_y = cv_file.getNode('stereoMapL_y').mat()
stereoMapR_x = cv_file.getNode('stereoMapR_x').mat()
stereoMapR_y = cv_file.getNode('stereoMapR_y').mat()


def undistortRectify(frameR, frameL):

    # Undistort and rectify images
    undistortedL= cv2.remap(frameL, stereoMapL_x, stereoMapL_y, cv2.INTER_LANCZOS4, cv2.BORDER_CONSTANT, 0)
    undistortedR= cv2.remap(frameR, stereoMapR_x, stereoMapR_y, cv2.INTER_LANCZOS4, cv2.BORDER_CONSTANT, 0)


    return undistortedR, undistortedL


# def undistorted(frameR, frameL):

#     # Load parameters attained from "calib_parameters.py"
#     ret = np.load("./Calibration/camera_params/ret.npy")
#     K = np.load("./Calibration/camera_params/K.npy")
#     dist = np.load("./Calibration/camera_params/dist.npy")
#     rvecs = np.load("./Calibration/camera_params/rvecs.npy")
#     tvecs = np.load("./Calibration/camera_params/tvecs.npy")


#     hR,wR = frameR.shape[:2]
#     hL,wL = frameL.shape[:2]
#     new_camera_matrixR, roiR = cv2.getOptimalNewCameraMatrix(K,dist,(wR,hR),1,(wR,hR))
#     new_camera_matrixL, roiL = cv2.getOptimalNewCameraMatrix(K,dist,(wL,hL),1,(wL,hL))

#     #Undistort images
#     frame_undistortedR = cv2.undistort(frameR, K, dist, None, new_camera_matrixR)
#     frame_undistortedL = cv2.undistort(frameL, K, dist, None, new_camera_matrixL)

#     ##Uncomment if you want help lines:
#     #frame_undistortedR = cv2.line(frame_undistortedR, (0,int(hR/2)), (wR,240), (0, 255, 0) , 5)
#     #frame_undistortedR = cv2.line(frame_undistortedR, (int(wR/2),0), (int(wR/2),hR), (0, 255, 0) , 5)
#     #frame_undistortedL = cv2.line(frame_undistortedL, (int(wL/2),0), (int(wL/2),hL), (0, 255, 0) , 5)
#     #frame_undistortedL = cv2.line(frame_undistortedL, (0,int(hL/2)), (wL,240), (0, 255, 0) , 5)
#     print(K)

#     return frame_undistortedR, frame_undistortedL