package com.amhsrobotics.ballradar.systems;

import com.amhsrobotics.ballradar.Main;
import com.amhsrobotics.ballradar.components.SplineComponent;
import com.amhsrobotics.ballradar.field.FieldGraph;
import com.amhsrobotics.ballradar.parametrics.Point2D;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class SplineSystem extends EntitySystem {

    FieldGraph field;

    public SplineSystem(FieldGraph field) {
        this.field = field;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> entities = Main.engine.getEntitiesFor(Family.all(SplineComponent.class).get());

        for(Entity e : entities) {

            SplineComponent sc = e.getComponent(SplineComponent.class);

//            ModelBuilder mb = new ModelBuilder();
//            mb.begin();
//            MeshPartBuilder meshBuilder;

            for(float i = 0; i <= 1.0; i += 0.02) {
                Point2D v2d = sc.spline.getPoint(i);
                Point2D v2d2 = sc.spline.getPoint(i+0.02);

//                mb.node().translation.set(new Vector3((float) v2d.x, 1, (float) v2d.y));
//                meshBuilder = mb.part(i + "", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material());
//                meshBuilder.sphere(2, 2, 2, 100, 100);


                field.splineSegment((float) v2d.x, (float) v2d.y, (float) v2d2.x, (float) v2d2.y);
            }

//            Model m = mb.end();
//            Main.engine.addEntity(EntityFactory.createSplineModel(m));

            Main.engine.removeEntity(e);
        }

    }
}
