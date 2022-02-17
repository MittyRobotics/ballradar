import subprocess

def setCameraProps():
    subprocess.check_call("v4l2-ctl -d /dev/video0 -c exposure_auto=3", shell=True)
    # subprocess.check_call("v4l2-ctl -d /dev/video0 -c exposure_auto=1 -c exposure_absolute=11 -c brightness=90 -c contrast=10", shell=True)
