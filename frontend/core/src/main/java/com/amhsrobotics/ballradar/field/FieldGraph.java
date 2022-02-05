package com.amhsrobotics.ballradar.field;

import com.amhsrobotics.ballradar.managers.CameraManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class FieldGraph {

    private ShapeRenderer renderer;
    private CameraManager camera;

    public static final int PIXELS_PER_METER = 50;
    public static final int MAX_PIXELS = 1000;

    public FieldGraph(CameraManager camera) {
        renderer = new ShapeRenderer();
        this.camera = camera;
    }

    public static Vector2 polarToWorldCoordinates(float angleInDegrees, float distanceInMeters) {

        double angleInRadians = Math.toRadians(angleInDegrees);

        double x = distanceInMeters * Math.sin(angleInRadians) * PIXELS_PER_METER;
        double y = -(distanceInMeters * Math.cos(angleInRadians) * PIXELS_PER_METER);

        return new Vector2((float) x, (float) y);
    }

    public void update() {
        renderer.setProjectionMatrix(camera.getSelectedCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(159/255f, 159/255f, 162/255f, 0.1f);

        for(int i = -(MAX_PIXELS / 2); i <= MAX_PIXELS / 2; i += PIXELS_PER_METER) {

            for(int j = -(MAX_PIXELS / 2); j <= MAX_PIXELS / 2; j += PIXELS_PER_METER) {

                renderer.line(new Vector3(i, 0, -(MAX_PIXELS / 2)), new Vector3(i, 0, MAX_PIXELS / 2)); // z
                renderer.line(new Vector3(-(MAX_PIXELS / 2), 0, j), new Vector3(MAX_PIXELS / 2, 0, j)); // x

            }
        }

        renderer.end();
    }
}
