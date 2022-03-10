package com.amhsrobotics.ballradar.systems;

import com.amhsrobotics.ballradar.Main;
import com.amhsrobotics.ballradar.components.BallComponent;
import com.amhsrobotics.ballradar.components.BallIndicatorComponent;
import com.amhsrobotics.ballradar.components.ModelComponent;
import com.amhsrobotics.ballradar.managers.EntityFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;

public class BallIndicatorSystem extends EntitySystem {

    @Override
    public void addedToEngine(Engine engine) {
        Gdx.app.log("BallIndicationSystem", "Initialized");

        super.addedToEngine(engine);
    }

    @Override
    public void removedFromEngine(Engine engine) {
    }

    @Override
    public void update(float deltaTime) {

        ImmutableArray<Entity> ball3d = Main.engine.getEntitiesFor(Family.all(BallComponent.class, ModelComponent.class).get());
        ImmutableArray<Entity> ball2d = Main.engine.getEntitiesFor(Family.all(BallIndicatorComponent.class).get());

        for(Entity e : ball2d) {
            Main.engine.removeEntity(e);
        }

        for(Entity e : ball3d) {
            ModelComponent mc = e.getComponent(ModelComponent.class);

        }

    }
}
