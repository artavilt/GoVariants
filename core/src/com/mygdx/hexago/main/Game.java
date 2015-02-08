package com.mygdx.hexago.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hexago.handlers.GameStateManager;

public class Game implements ApplicationListener {

	public static final String TITLE = "Walls";
	public static int V_WIDTH = 1024;
	public static int V_HEIGHT = 768;
	public static final int SCALE = 1;

	public static final float STEP = 1/60f;
	private float accum;

	private OrthographicCamera cam;

	private GameStateManager gsm;

	public OrthographicCamera getCam() { return cam; }

	public void create() {
		//sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		gsm = new GameStateManager(this);
	}

	public void render() {
		accum += Gdx.graphics.getDeltaTime();
		while( accum >= STEP ){
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
		}
	}
	public void dispose() {}

	public void resize(int width, int height) {
		V_WIDTH = width;
		V_HEIGHT = height;
		cam.setToOrtho(false, width, height);
		gsm.resize(width, height);
	}
	public void pause() {}
	public void resume() {}


}
