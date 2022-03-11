package com.amhsrobotics.ballradar.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ModelComponent implements Component {

    public Model model;
    public ModelInstance instance;
    public Vector3 position;

    public ModelComponent(Model model, float x, float y, float z) {
        this.position = new Vector3(x, y, z);
        this.model = model;
        instance = new ModelInstance(model, new Matrix4().setToTranslation(x, y, z));
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        instance.transform.setToTranslation(x, y, z);
    }
}
