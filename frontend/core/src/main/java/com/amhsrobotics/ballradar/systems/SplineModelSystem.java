package com.amhsrobotics.ballradar.systems;

import com.amhsrobotics.ballradar.Main;
import com.amhsrobotics.ballradar.components.ModelComponent;
import com.amhsrobotics.ballradar.components.SplineComponent;
import com.amhsrobotics.ballradar.components.SplineModelComponent;
import com.amhsrobotics.ballradar.field.FieldGraph;
import com.amhsrobotics.ballradar.managers.EntityFactory;
import com.amhsrobotics.ballradar.parametrics.Point2D;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class SplineModelSystem extends EntitySystem {


    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = Main.engine.getEntitiesFor(Family.all(SplineModelComponent.class).get());

        for(Entity e : entities) {

            ModelComponent mc = e.getComponent(ModelComponent.class);
//            mc.instance.model.dispose();
            Main.engine.removeEntity(e);
        }

    }
}
