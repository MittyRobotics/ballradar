package com.amhsrobotics.ballradar.systems;

import com.amhsrobotics.ballradar.Main;
import com.amhsrobotics.ballradar.components.ModelComponent;
import com.amhsrobotics.ballradar.components.SplineComponent;
import com.amhsrobotics.ballradar.field.FieldGraph;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

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
        HashMap<SplineComponent, Float> distances = new HashMap<>();

        if(Main.robotPosition != null) {

            for(Entity e : entities) {

                SplineComponent sc = e.getComponent(SplineComponent.class);

                Entity ball = sc.parentBall;
                Vector3 pos = ball.getComponent(ModelComponent.class).position;
                sc.updateEndpoint(pos.x, pos.z);

                distances.put(sc, FieldGraph.pixelsToMeters(pos.dst(Main.robotPosition)));

            }

            Map.Entry<SplineComponent, Float> min = null;
            for (Map.Entry<SplineComponent, Float> entry : distances.entrySet()) {
                if (min == null || min.getValue() > entry.getValue()) {
                    min = entry;
                }
            }
            if(min != null) {
                field.solidSpline(min.getKey().spline);

                for(SplineComponent comp : distances.keySet()) {
                    if(comp.spline != min.getKey().spline) {
                        field.dottedSpline(comp.spline);
                    }
                }
            }
        }

    }

    public static Entity getSplineByEntity(Entity e) {
        ImmutableArray<Entity> entities = Main.engine.getEntitiesFor(Family.all(SplineComponent.class).get());

        for(Entity entity : entities) {
            if(entity.getComponent(SplineComponent.class).parentBall == e) {
                return entity;
            }
        }
        return null;
    }
}
