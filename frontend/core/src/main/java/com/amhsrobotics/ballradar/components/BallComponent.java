package com.amhsrobotics.ballradar.components;

import com.badlogic.ashley.core.Component;

public class BallComponent implements Component {

    public int id;
    public boolean updated = false;

    public BallComponent() {
        id = 0;
    }

    public BallComponent(int id) {
        this.id = id;
    }
}
