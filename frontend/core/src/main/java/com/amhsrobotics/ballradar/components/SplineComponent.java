package com.amhsrobotics.ballradar.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.mittyrobotics.pathfollowing.Angle;
import com.github.mittyrobotics.pathfollowing.Pose2D;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSpline;
import com.github.mittyrobotics.pathfollowing.Vector2D;

public class SplineComponent implements Component {

    public QuinticHermiteSpline spline;
    public Entity parentBall;

    public SplineComponent(Entity parentBall, float x, float z) {
        this.parentBall = parentBall;

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
