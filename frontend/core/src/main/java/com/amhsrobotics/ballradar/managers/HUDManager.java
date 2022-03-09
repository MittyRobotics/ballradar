package com.amhsrobotics.ballradar.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HUDManager {

    private OrthographicCamera hudcam;
    private ShapeRenderer renderer;
    private SpriteBatch batch;
    private BitmapFont font;

    public HUDManager() {
        hudcam = new OrthographicCamera();
        hudcam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        font = new BitmapFont(false);
    }

    public void drawLoading() {
        hudcam.update();

        batch.setProjectionMatrix(hudcam.combined);
        batch.begin();

        font.draw(batch, "Loading Models...", Gdx.graphics.getWidth() - 125, Gdx.graphics.getHeight() - 20);

        batch.end();
    }

    public void render() {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(new Color(51/255f, 51/255f, 51/255f, 1f));

        renderer.rect(0, Gdx.graphics.getHeight() - 50, Gdx.graphics.getWidth(), 50);
        renderer.end();
    }
}
