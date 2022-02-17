package com.amhsrobotics.ballradar.components;

import com.amhsrobotics.ballradar.parametrics.Angle;
import com.amhsrobotics.ballradar.parametrics.Pose2D;
import com.amhsrobotics.ballradar.parametrics.QuinticHermiteSpline;
import com.amhsrobotics.ballradar.parametrics.Vector2D;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class SplineComponent implements Component {

    public QuinticHermiteSpline spline;
    public Entity e;

    public SplineComponent(Entity e, float x, float z) {
        this.e = e;

        spline = new QuinticHermiteSpline(
                new Pose2D(0, 0, 90),
                new Pose2D(x, z, 0),
                new Vector2D(new Angle(Math.toRadians(270)), 100),
                new Vector2D(new Angle(x, z), 100)
        );
    }

    public void updateEndpoint(float x, float z) {
        spline = new QuinticHermiteSpline(
                new Pose2D(0, 0, 90),
                new Pose2D(x, z, 0),
                new Vector2D(new Angle(Math.toRadians(270)), 100),
                new Vector2D(new Angle(x, z), 100)
        );
    }

}
