package com.amhsrobotics.ballradar;

import com.amhsrobotics.ballradar.field.FieldGraph;
import com.amhsrobotics.ballradar.field.NetworkTablesServer;
import com.amhsrobotics.ballradar.managers.CameraManager;
import com.amhsrobotics.ballradar.managers.EntityFactory;
import com.amhsrobotics.ballradar.managers.HUDManager;
import com.amhsrobotics.ballradar.managers.ModelFactory;
import com.amhsrobotics.ballradar.systems.RenderSystem;
import com.amhsrobotics.ballradar.systems.SplineSystem;
import com.amhsrobotics.ballradar.systems.VisionTrackingSystem;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;

public class Main extends ApplicationAdapter {

	public static Engine engine;

	private ModelBatch batch;
	private Environment env;

	private CameraManager cam;
	private HUDManager hud;

	private FieldGraph fg;

	private Entity loadingEntity;
	private boolean loading = true;

	@Override
	public void create() {
		engine = new Engine();
		batch = new ModelBatch();

		cam = new CameraManager();
		cam.switchToTopView();
		Gdx.input.setInputProcessor(new InputMultiplexer(
				new InputCore(cam),
				cam.getPersCameraInputController()
		));

		fg = new FieldGraph(cam);

		initEnvironment();
		initSystems();
		initEntities();

		hud = new HUDManager();

		NetworkTablesServer.run();
		ModelFactory.loadAssets();
	}

	private void initEnvironment() {
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
		env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	private void initSystems() {
		engine.addSystem(new VisionTrackingSystem());
		engine.addSystem(new SplineSystem(fg));
		engine.addSystem(new RenderSystem(batch, env));
//		engine.addSystem(new SplineModelSystem());
	}

	private void initEntities() {

		loadingEntity = EntityFactory.createPlaceholder();
		engine.addEntity(loadingEntity);
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(34 / 255f, 34 / 255f, 36 / 255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		Gdx.gl.glEnable(GL30.GL_TEXTURE_2D);
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

		if(loading && ModelFactory.assetManager.update()) {
			engine.addEntity(EntityFactory.createRobot());
			engine.removeEntity(loadingEntity);
			loadingEntity = null;
			loading = false;
		}

		if(loading) {
			hud.drawLoading();
		}

		fg.update();

		batch.begin(cam.getSelectedCamera());
		engine.update(Gdx.graphics.getDeltaTime());
		batch.end();

		cam.update();

		Gdx.gl.glDisable(GL30.GL_BLEND);
		Gdx.gl.glDisable(GL20.GL_TEXTURE_2D);
	}

	@Override
	public void dispose() {
		batch.dispose();
		ModelFactory.dispose();
	}
	@Override
	public void resize(int width, int height) {
		cam.resize(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

}