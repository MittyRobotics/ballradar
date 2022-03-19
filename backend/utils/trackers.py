from collections import OrderedDict
import numpy as np
from scipy.spatial import distance as dist

# (centroid coords), ((bounding box coords), color, confidence, cam_id))


class CentroidTracker():

    def __init__(self, maxDisappeared=40, idStartingPoint=0):

        self.uniqueID = idStartingPoint
        
        self.balls = OrderedDict()
        self.off_screen_frames = OrderedDict()

        self.maxDisappearedFrames = maxDisappeared

    def register(self, centroid, bbox):

        self.balls[self.uniqueID] = (centroid, bbox)
        self.off_screen_frames[self.uniqueID] = 0
        self.uniqueID += 1

    def remove(self, id):

        del self.balls[id]
        del self.off_screen_frames[id]

    def get_data(self):
        return self.balls

    def update(self, bboxes, fromBulkFunction=False):

        if len(bboxes) == 0:

            for oid in list(self.off_screen_frames.keys()):
                self.off_screen_frames[oid] += 1

                if self.off_screen_frames[oid] > self.maxDisappearedFrames:
                    self.remove(oid)

            return self.balls

        new_centroids = np.zeros((len(bboxes), 2), dtype="int")
        bb_save = {}

        for i, ((x1, y1, x2, y2), color, conf, cam) in enumerate(bboxes):
            cx = int((x1 + x2) / 2.0)
            cy = int((y1 + y2) / 2.0)
            new_centroids[i] = (cx, cy)
            bb_save[i] = ((x1, y1, x2, y2), color, conf, cam)

        if len(self.balls) == 0:
            for i in range(0, len(new_centroids)):
                if not fromBulkFunction:
                    self.register(new_centroids[i], bb_save[i])
        else:
            oids = list(self.balls.keys())
            prev_centroids = [i[0] for i in list(self.balls.values())]

            distance = dist.cdist(np.array(prev_centroids), new_centroids)
            rows = distance.min(axis=1).argsort()
            cols = distance.argmin(axis=1)[rows]

            usedRows = set()
            usedCols = set()

            for (row, col) in zip(rows, cols):
                if row in usedRows or col in usedCols:
                    continue

                oid = oids[row]
                self.balls[oid] = (new_centroids[col], bb_save[col])
                self.off_screen_frames[oid] = 0

                usedRows.add(row)
                usedCols.add(col)

            unusedRows = set(range(0, distance.shape[0])).difference(usedRows)
            unusedCols = set(range(0, distance.shape[1])).difference(usedCols)

            if distance.shape[0] >= distance.shape[1]:
                for row in unusedRows:
                    oid = oids[row]
                    self.off_screen_frames[oid] += 1

                    if self.off_screen_frames[oid] > self.maxDisappearedFrames:
                        self.remove(oid)

            else:
                for col in unusedCols:
                    if not fromBulkFunction:
                        self.register(new_centroids[col], bb_save[col])

        return self.balls

# (centroid coords), ((bounding box coords), color, confidence, cam_id))


class StereoTracker():

    def __init__(self):
        self.ball_pairs = []

    def get_pairs(self):
        return self.ball_pairs

    def update(self, leftTracker, rightTracker):
        self.ball_pairs.clear()
        leftBalls = leftTracker.get_data().items()
        rightBalls = rightTracker.get_data().items()

        while len(leftBalls) != 0:

            if len(rightBalls) == 0:
                break

            #format: (oid, ((cx, cy), ((bx1, by1, bx2, by2), color, confidence, cam_id)))
            leftmost_left_centroid = min(leftBalls, key = lambda t: t[1][0][0])
            leftmost_right_centroid = min(rightBalls, key = lambda t: t[1][0][0])

            self.ball_pairs.append((leftmost_left_centroid, leftmost_right_centroid))

            leftBalls.remove(leftmost_left_centroid)
            rightBalls.remove(leftmost_right_centroid)

            # todo: print out y values of both balls, to see if they are on same line


