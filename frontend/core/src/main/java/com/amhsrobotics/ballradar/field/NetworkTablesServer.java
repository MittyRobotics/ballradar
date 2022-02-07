package com.amhsrobotics.ballradar.field;

import edu.wpi.first.networktables.NetworkTableInstance;

public class NetworkTablesServer {

    private static NetworkTableInstance inst = NetworkTableInstance.getDefault();

    public static void run() {
        inst.startServer("ballradarpersist", "127.0.0.1", 1735);
        System.out.println("Started network tables");

    }

    public static String getBallData() {
        return inst.getTable("ballradar").getEntry("balldata").getString("none");
    }

}
