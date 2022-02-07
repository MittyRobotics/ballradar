package com.amhsrobotics.ballradar.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

public class CameraManager {

    private final int FOV = 67;
    private final int ORTHO_DIVISOR = 11;
    private final float INTERP_CONSTANT = 0.2f;

    private PerspectiveCamera persCam;
    private CameraInputController persCamController;
    private OrthographicCamera topCam;
    private Camera selectedCamera;


    Vector3 assistPosition;
    Vector3 assistAngle;
    Vector3 assistAngleUp;

    Vector3 topPosition;
    Vector3 topAngle;
    Vector3 topAngleUp;

    Vector3 center;

    private enum CameraMovement {
        ASSIST, TOPDOWN, ELASTIC_TOPDOWN, ELASTIC_ASSIST, NONE
    }

    private CameraMovement interpolating = CameraMovement.NONE;


    public CameraManager() {

        persCam = new PerspectiveCamera(FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        persCam.position.set(0, 80f, 0f);
        persCam.lookAt(0, 0, 0);
        persCam.near = 1f;
        persCam.far = 760f;
        persCam.update();

        topCam = new OrthographicCamera();
        topCam.setToOrtho(false, Gdx.graphics.getWidth() / ORTHO_DIVISOR, Gdx.graphics.getHeight() / ORTHO_DIVISOR);
        topCam.position.set(0, 80f, 0);
        topCam.lookAt(0, 0, 0);
        topCam.update();

        topPosition = new Vector3(persCam.position);
        topAngle = new Vector3(persCam.direction);
        topAngleUp = new Vector3(persCam.up);

        assistPosition = new Vector3(0, 80, persCam.position.z + 40);
        assistAngle = new Vector3(persCam.direction.x, persCam.direction.y, persCam.direction.z - 1);
        assistAngleUp = new Vector3(persCam.up.x, persCam.up.y , persCam.up.z - 1);

        center = new Vector3(0, 80f, 0);

        selectedCamera = topCam;

        persCamController = new CameraInputController(persCam);

    }

    public CameraInputController getPersCameraInputController() {
        return persCamController;
    }

    public void switchToAssistView() {
        selectedCamera = persCam;
        interpolating = CameraMovement.ASSIST;
    }

    public void switchToTopView() {
        interpolating = CameraMovement.TOPDOWN;
    }

    public void update() {
        selectedCamera.update();

        if(interpolating != CameraMovement.NONE) {
            if(interpolating == CameraMovement.ASSIST) {
                if(persCam.direction.dst(assistAngle) < 0.1) {
                    interpolating = CameraMovement.ELASTIC_ASSIST;
                }
                persCam.position.interpolate(assistPosition, INTERP_CONSTANT, Interpolation.sine);
                persCam.direction.interpolate(assistAngle, INTERP_CONSTANT, Interpolation.sine);
                persCam.up.interpolate(assistAngleUp, INTERP_CONSTANT, Interpolation.sine);
            } else if(interpolating == CameraMovement.TOPDOWN) {
                if(persCam.direction.dst(topAngle) < 0.1) {
                    selectedCamera = topCam;
                    interpolating = CameraMovement.ELASTIC_TOPDOWN;
                }
                persCam.position.interpolate(topPosition, INTERP_CONSTANT, Interpolation.sine);
                persCam.direction.interpolate(topAngle, INTERP_CONSTANT, Interpolation.sine);
                persCam.up.interpolate(topAngleUp, INTERP_CONSTANT, Interpolation.sine);
            } else if(interpolating == CameraMovement.ELASTIC_TOPDOWN) {
                topCam.position.interpolate(center, INTERP_CONSTANT, Interpolation.sine);
            } else if(interpolating == CameraMovement.ELASTIC_ASSIST) {
//                persCam.position.interpolate(assistPosition, 0.1f, Interpolation.sine);
//                persCam.direction.interpolate(assistAngle, 0.1f, Interpolation.sine);
//                persCam.up.interpolate(assistAngleUp, 0.1f, Interpolation.sine);
            }
        }
    }

    public void translateOrtho(float deltaX, float deltaY) {
        topCam.translate(deltaX, 0, deltaY);
    }

    public void resize(int width, int height) {
        persCam.viewportWidth = width;
        persCam.viewportHeight = height;

        topCam.viewportWidth = width / ORTHO_DIVISOR;
        topCam.viewportHeight = height / ORTHO_DIVISOR;
    }

    public Camera getSelectedCamera() {
        return selectedCamera;
    }

}
