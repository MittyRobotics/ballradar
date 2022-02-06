package com.amhsrobotics.ballradar.systems;

import com.amhsrobotics.ballradar.components.ModelComponent;
import com.amhsrobotics.ballradar.field.FieldGraph;
import com.amhsrobotics.ballradar.field.NetworkTablesServer;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;

public class RenderSystem extends IteratingSystem {

    private ComponentMapper<ModelComponent> mc = ComponentMapper.getFor(ModelComponent.class);

    private ModelBatch batch;
    private Environment environment;

    public RenderSystem(ModelBatch batch, Environment environment) {
        super(Family.all(ModelComponent.class).get());

        this.batch = batch;
        this.environment = environment;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ModelComponent model = mc.get(entity);
        batch.render(model.instance, environment);

    }
}
