package com.amhsrobotics.ballradar.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class BallIndicatorComponent implements Component {

    public Sprite ball;

    public BallIndicatorComponent(Texture texture, int x, int y) {
        ball = new Sprite(texture, x, y);
        ball.setOriginCenter();
        ball.setSize(40, 40);

    }
}
