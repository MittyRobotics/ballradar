package com.amhsrobotics.ballradar;

import com.amhsrobotics.ballradar.managers.CameraManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public class InputCore implements InputProcessor {

    private CameraManager cam;

    public InputCore(CameraManager cam) {
        this.cam = cam;
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.B) {
            cam.switchToAssistView();
        }
        if(keycode == Input.Keys.V) {
            cam.switchToTopView();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if(cam.getSelectedCamera() instanceof OrthographicCamera) {
            float x = Gdx.input.getDeltaX() * ((OrthographicCamera) cam.getSelectedCamera()).zoom;
            float y = Gdx.input.getDeltaY()  * ((OrthographicCamera) cam.getSelectedCamera()).zoom;

            cam.translateOrtho(-x, -y);
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if(cam.getSelectedCamera() instanceof OrthographicCamera) {
            ((OrthographicCamera) cam.getSelectedCamera()).zoom += amountY * ((OrthographicCamera) cam.getSelectedCamera()).zoom * 0.1f;
        }
        return false;
    }

}
