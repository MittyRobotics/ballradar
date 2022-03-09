package com.amhsrobotics.ballradar.managers;

import com.amhsrobotics.ballradar.Main;
import com.amhsrobotics.ballradar.components.BallComponent;
import com.amhsrobotics.ballradar.components.ModelComponent;
import com.amhsrobotics.ballradar.components.SplineComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class EntityFactory {

    public static Entity createEntity(Model model, int id, float x, float y, float z) {
        Entity entity = new Entity();

        entity.add(new ModelComponent(model, x, y, z));
        entity.add(new BallComponent(id));

        return entity;
    }

    public static Entity createPlaceholder() {
        Entity entity = new Entity();

        entity.add(new ModelComponent(ModelFactory.generateCube(), 0, 0, 0));

        return entity;
    }

    public static Entity createRobot() {
        Entity entity = new Entity();
        Model m = ModelFactory.loadRobot();

        BoundingBox b = new BoundingBox();
        b = m.calculateBoundingBox(b);

        Main.robotPosition = new Vector3(-b.getWidth() / 2, 0, -b.getDepth() / 2);

        ModelComponent mc = new ModelComponent(m, -b.getWidth() / 2 + 15, 0, b.getDepth() / 2 - 30);
        Quaternion q = new Quaternion();
        q.setEulerAngles(270, 0, 0);
        mc.instance.transform.rotate(q);
//        mc.instance.transform.scale(1.26f, 1.26f, 1.26f);

        entity.add(mc);

        return entity;
    }

    public static Entity createSpline(Entity e, float x, float z) {
        Entity entity = new Entity();
        SplineComponent sc = new SplineComponent(e, x, z);

        entity.add(sc);

        return entity;
    }

}
