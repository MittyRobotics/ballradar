package com.amhsrobotics.ballradar.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent implements Component {

    float x;
    float y;
    float z;

    public PositionComponent(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PositionComponent() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
}
