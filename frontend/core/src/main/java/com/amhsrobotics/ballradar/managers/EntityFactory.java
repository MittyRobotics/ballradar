package com.amhsrobotics.ballradar.managers;

import com.amhsrobotics.ballradar.components.BallComponent;
import com.amhsrobotics.ballradar.components.ModelComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.collision.BoundingBox;

public class EntityFactory {

    public static Entity createEntity(Model model, float x, float y, float z) {
        Entity entity = new Entity();

        entity.add(new ModelComponent(model, x, y, z));
        entity.add(new BallComponent());

        return entity;
    }

    public static Entity createRobot() {
        Entity entity = new Entity();
        Model m = ModelFactory.generateRobot();

        BoundingBox b = new BoundingBox();
        b = m.calculateBoundingBox(b);

        ModelComponent mc = new ModelComponent(m, 0, b.getHeight() / 2, 0);
        mc.instance.userData = "robot";
        Quaternion q = new Quaternion();
        q.setEulerAngles(90, 0, 0);
        mc.instance.transform.rotate(q);
        entity.add(mc);

        return entity;
    }
}