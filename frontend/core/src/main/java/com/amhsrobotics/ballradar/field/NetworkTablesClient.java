package com.amhsrobotics.ballradar.field;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NetworkTablesClient {

    private NetworkTableInstance instance;
    private NetworkTable table;
    private NetworkTableEntry angle, distance;

    private double test = 0;

    public NetworkTablesClient() {
        instance = NetworkTableInstance.getDefault();
        table = instance.getTable("ballradar");

        angle = table.getEntry("angle");
        distance = table.getEntry("distance");

    }

    public double getAngle() {
        return angle.getDouble(0.0);
    }

    public double getDistance() {
        return distance.getDouble(0.0);
    }

    public void incrementDistanceTest() {
        test += 10;
        distance.setDouble(test);
    }
}
