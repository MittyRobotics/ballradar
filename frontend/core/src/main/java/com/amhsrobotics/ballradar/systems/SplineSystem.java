package com.amhsrobotics.ballradar.systems;

import com.amhsrobotics.ballradar.Main;
import com.amhsrobotics.ballradar.components.ModelComponent;
import com.amhsrobotics.ballradar.components.SplineComponent;
import com.amhsrobotics.ballradar.field.FieldGraph;
import com.amhsrobotics.ballradar.parametrics.Point2D;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class SplineSystem extends EntitySystem {

    FieldGraph field;

    public SplineSystem(FieldGraph field) {
        this.field = field;
    }

    @Override
    public void addedToEngine(Engine engine) {
        Gdx.app.log("SplineSystem", "Initialized");

        super.addedToEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = Main.engine.getEntitiesFor(Family.all(SplineComponent.class).get());

        for(Entity e : entities) {

            SplineComponent sc = e.getComponent(SplineComponent.class);

            Entity ball = sc.e;
            Vector3 pos = ball.getComponent(ModelComponent.class).position;
            sc.updateEndpoint(pos.x, pos.z);

            for(float i = 0; i <= 1.0; i += 0.02) {
                Point2D v2d = sc.spline.getPoint(i);
                Point2D v2d2 = sc.spline.getPoint(i+0.02);


                field.splineSegment((float) v2d.x, (float) v2d.y, (float) v2d2.x, (float) v2d2.y);
            }

//            Main.engine.removeEntity(e);
        }

    }

    public static Entity getSplineByEntity(Entity e) {
        ImmutableArray<Entity> entities = Main.engine.getEntitiesFor(Family.all(SplineComponent.class).get());

        for(Entity entity : entities) {
            if(entity.getComponent(SplineComponent.class).e == e) {
                return entity;
            }
        }
        return null;
    }
}
