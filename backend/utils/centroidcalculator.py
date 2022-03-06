from collections import OrderedDict
import numpy as np
from scipy.spatial import distance as dist

# (centroid coords), ((bounding box coords), color, confidence))
class CentroidTracker():
    
    def __init__(self, maxDisappeared=10):

        self.uniqueID = 0
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

    
    def update(self, bboxes):
        
        if len(bboxes) == 0:

            for oid in list(self.off_screen_frames.keys()):
                self.off_screen_frames[oid] += 1

                if self.off_screen_frames[oid] > self.maxDisappearedFrames:
                    self.remove(oid)

            return self.balls

        new_centroids = np.zeros((len(bboxes), 2), dtype="int")
        bb_save = {}

        for i, ((x1, y1, x2, y2), color, conf) in enumerate(bboxes):
            cx = int((x1 + x2) / 2.0)
            cy = int((y1 + y2) / 2.0)
            new_centroids[i] = (cx, cy)
            bb_save[i] = ((x1, y1, x2, y2), color, conf)

        if len(self.balls) == 0:
            for i in range(0, len(new_centroids)):
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
                    self.register(new_centroids[col], bb_save[col])

        return self.balls