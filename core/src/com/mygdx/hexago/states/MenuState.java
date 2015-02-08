package com.mygdx.hexago.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hexago.handlers.GameStateManager;


public class MenuState extends GameState{

    protected Stage stage;
    protected Skin skin;
    private Image background;

    public MenuState(GameStateManager gsm){
        super(gsm);
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = gsm.getSkin();
        Table table = new Table(skin);
        table.setPosition(160, 72);
        background = new Image(new Texture("res/title.jpg"));
        stage.addActor(background);
    }

    public void focus(){
        Gdx.input.setInputProcessor(stage);
    }

    public void resize(int width, int height){}

    public void update(float dt){}

    public void render(){
        Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        stage.act();
        stage.draw();
    }
    public void dispose(){}
}
