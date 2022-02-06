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
import com.badlogic.gdx.math.Vector2;

public class VisionTrackingSystem extends EntitySystem {

    private static final double MIN_CONFIDENCE = 0.8;

    @Override
    public void addedToEngine(Engine engine) {
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
    }

    @Override
    public void update(float deltaTime) {

        ImmutableArray<Entity> entities = Main.engine.getEntitiesFor(Family.all(BallComponent.class, ModelComponent.class).get());

        for(Entity e : entities) {
            Main.engine.removeEntity(e);
        }

        String data = NetworkTablesServer.getBallData();

        if(data != null && !data.equals("none")) {
            String[] balls = data.stripTrailing().split(" ");

            for(String ball : balls) {
                String[] props = ball.split(",");

                if(props.length == 0) {
                    continue;
                }

                double confidence = Double.parseDouble(props[3]);
                String color = props[2];
                float distanceMeters = Float.parseFloat(props[0]);
                float angleDeg = Float.parseFloat(props[1]);

                if(confidence >= MIN_CONFIDENCE) {
                    Vector2 vec = FieldGraph.polarToWorldCoordinates(angleDeg, distanceMeters);
                    Main.engine.addEntity(
                            EntityFactory.createEntity(
                                    ModelFactory.generateBall(color.equals("blueball") ? ModelFactory.BallType.BLUE : ModelFactory.BallType.RED), vec.x, 4.75f, vec.y
                            )
                    );
                }
            }
        }
    }
}
