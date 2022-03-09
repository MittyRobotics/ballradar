package com.amhsrobotics.ballradar.systems;

import com.amhsrobotics.ballradar.Main;
import com.amhsrobotics.ballradar.components.BallComponent;
import com.amhsrobotics.ballradar.components.ModelComponent;
import com.amhsrobotics.ballradar.field.FieldGraph;
import com.amhsrobotics.ballradar.field.NetworkTablesServer;
import com.amhsrobotics.ballradar.managers.EntityFactory;
import com.amhsrobotics.ballradar.managers.ModelFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class VisionTrackingSystem extends EntitySystem {

    private static final double MIN_CONFIDENCE = 0.8;
    private static String storedData = null;
    private static int counter = 0;
    private static final int counterMax = 15;

    @Override
    public void addedToEngine(Engine engine) {
        Gdx.app.log("VisionTrackingSystem", "Initialized");

        super.addedToEngine(engine);
    }

    @Override
    public void removedFromEngine(Engine engine) {
    }

    @Override
    public void update(float deltaTime) {

        ImmutableArray<Entity> entities = Main.engine.getEntitiesFor(Family.all(BallComponent.class, ModelComponent.class).get());

        for(Entity e : entities) {
            BallComponent bc = e.getComponent(BallComponent.class);
            bc.updated = false;
        }

        String data = NetworkTablesServer.getBallData();

        if(data == null || data.equals("none") || data.equals("")) {
            counter++;
            if(counter >= counterMax) {
                storedData = null;
                counter = 0;
            }
        } else {
            storedData = data;
        }

        if(storedData != null && !storedData.equals("none")) {
            System.out.println(storedData);
            String[] balls = storedData.stripTrailing().split(" ");

            for(String ball : balls) {
                String[] props = ball.split(",");

                if(props.length == 0 || props.length == 1) {
                    continue;
                }

                int ballId = Integer.parseInt(props[0]);
                double confidence = Double.parseDouble(props[4]);
                String color = props[3];
                float distanceMeters = Float.parseFloat(props[1]);
                float angleDeg = Float.parseFloat(props[2]);

                boolean idFound = false;
                for(Entity e : entities) {
                    BallComponent c = e.getComponent(BallComponent.class);
                    if(c.id == ballId) {
                        ModelComponent mc = e.getComponent(ModelComponent.class);
                        Vector2 vec = FieldGraph.polarToWorldCoordinates(angleDeg, distanceMeters);
                        mc.setPosition(vec.x, 4.75f, vec.y);
                        c.updated = true;
                        idFound = true;
                    }
                }

                if(!idFound) {
                    Vector2 vec = FieldGraph.polarToWorldCoordinates(angleDeg, distanceMeters);
                    Entity newEntity = EntityFactory.createEntity(
                                    ModelFactory.generateBall(color.equals("blueball") ? ModelFactory.BallType.BLUE : ModelFactory.BallType.RED), ballId, vec.x, 4.75f, vec.y
                    );
                    Main.engine.addEntity(newEntity);
                    Main.engine.addEntity(
                            EntityFactory.createSpline(newEntity, vec.x, vec.y)
                    );
                }

            }
        }
        for(Entity e : entities) {
            BallComponent bc = e.getComponent(BallComponent.class);
            if(!bc.updated) {
                Main.engine.removeEntity(e);
                Entity spline = SplineSystem.getSplineByEntity(e);
                if(spline != null) Main.engine.removeEntity(spline);
            }
        }

    }
}
