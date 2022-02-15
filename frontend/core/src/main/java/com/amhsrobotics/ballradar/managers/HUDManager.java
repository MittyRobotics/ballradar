package com.amhsrobotics.ballradar.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HUDManager {

    private OrthographicCamera hudcam;
    private SpriteBatch batch;
    private BitmapFont font;

    public HUDManager() {
        hudcam = new OrthographicCamera();
        hudcam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();

        font = new BitmapFont(false);
    }

    public void drawLoading() {
        hudcam.update();

        batch.setProjectionMatrix(hudcam.combined);
        batch.begin();

        font.draw(batch, "Loading Models...", Gdx.graphics.getWidth() - 125, Gdx.graphics.getHeight() - 20);

        batch.end();
    }
}
