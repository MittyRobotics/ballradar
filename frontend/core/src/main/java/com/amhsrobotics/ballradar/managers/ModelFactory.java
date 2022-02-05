package com.amhsrobotics.ballradar.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import java.util.ArrayList;

public class ModelFactory {

    private static ModelBuilder modelBuilder;
    private static AssetManager assetManager;
    private static ArrayList<Model> models;
    private static ObjLoader objLoader;

    public enum BallType {
        BLUE, RED
    }

    static {
        modelBuilder = new ModelBuilder();
        assetManager = new AssetManager();
        models = new ArrayList<>();


//        assetManager.load("robot.g3db", Model.class);
//        assetManager.finishLoading();

        objLoader = new ObjLoader();
    }

    public static Model generateBall(BallType type) {

        Model m = modelBuilder.createSphere(
                9.5f, 9.5f, 9.5f, 100, 100,
                new Material(ColorAttribute.createDiffuse(
                        type == BallType.BLUE ?
                            new Color(30/255f, 81/255f, 255/255f, 1f) :
                            new Color(255/255f, 89/255f, 30/255f, 1f)
                )),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        models.add(m);

        return m;
    }

    public static Model generateRobot() {
        Model m = objLoader.loadModel(Gdx.files.internal("robot.obj"), true);
        models.add(m);

        return m;
    }

    public static void dispose() {
        for(Model m : models) {
            m.dispose();
        }
        models.clear();
        assetManager.dispose();
    }
}
