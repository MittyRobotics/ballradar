package com.amhsrobotics.ballradar.field;

import com.amhsrobotics.ballradar.managers.CameraManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.mittyrobotics.pathfollowing.Point2D;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSpline;

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

    public static float pixelsToMeters(float pixels) {
        return pixels / PIXELS_PER_METER;
    }

    public void beginRenderer() {
        renderer.setProjectionMatrix(camera.getSelectedCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
    }

    public void endRenderer() {
        renderer.end();
    }

    public void update() {
        Gdx.gl20.glLineWidth(1);
        beginRenderer();

        renderer.setColor(159/255f, 159/255f, 162/255f, 0.1f);

        for(int i = -(MAX_PIXELS / 2); i <= MAX_PIXELS / 2; i += PIXELS_PER_METER) {

            for(int j = -(MAX_PIXELS / 2); j <= MAX_PIXELS / 2; j += PIXELS_PER_METER) {

                renderer.line(new Vector3(i, 0, -(MAX_PIXELS / 2)), new Vector3(i, 0, MAX_PIXELS / 2)); // z
                renderer.line(new Vector3(-(MAX_PIXELS / 2), 0, j), new Vector3(MAX_PIXELS / 2, 0, j)); // x
            }
        }

        endRenderer();

    }

    public void solidSpline(QuinticHermiteSpline spline) {
        for(float i = 0; i <= 1.0; i += 0.02) {
            Point2D v2d = spline.getPoint(i);
            Point2D v2d2 = spline.getPoint(i+0.02);


            drawLine(v2d, v2d2);
        }
    }

    private void drawLine(Point2D v2d, Point2D v2d2) {
        renderer.setProjectionMatrix(camera.getSelectedCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        Gdx.gl20.glLineWidth(5);
        renderer.line(new Vector3((float) v2d.x, 0, (float) v2d.y), new Vector3((float) v2d2.x, 0, (float) v2d2.y));

        renderer.end();
    }

    public void dottedSpline(QuinticHermiteSpline spline) {
        for(float i = 0; i <= 1.0; i += 0.02) {
            Point2D v2d = spline.getPoint(i);
            Point2D v2d2 = spline.getPoint(i+0.01);


            drawLine(v2d, v2d2);
        }
    }
}
