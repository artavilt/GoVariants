package com.mygdx.hexago.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hexago.boardcore.BoardConfig;
import com.mygdx.hexago.handlers.GameStateManager;


public class GUIMenu extends MenuState{

    private BoardConfig boardConfig;
    private Skin skin;

    public GUIMenu(GameStateManager gsm){
        super(gsm);
        boardConfig = new BoardConfig();
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // SINGLE PLAYER GAME
        TextButton playButton = new TextButton("Single Player", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {startSinglePlayerGame();
            }
        });
        playButton.setPosition(300, 300);
        playButton.setSize(150, 40);
        stage.addActor(playButton);

        // OPTIONS MENU
        TextButton optionsButton = new TextButton("Options", skin);
        optionsButton.addListener( new ClickListener() {
            @Override
            public void touchUp( InputEvent e, float x, float y, int point, int button ){openOptions();
            }
        });
        optionsButton.setPosition(300, 200);
        optionsButton.setSize(150, 40);
        stage.addActor(optionsButton);

        // CLIENT GAME
        TextButton clientButton = new TextButton("Join Network Game", skin);
        clientButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {startClientGame();
            }
        });
        clientButton.setPosition(600, 300);
        clientButton.setSize(150, 40);
        stage.addActor(clientButton);

        // SERVER GAME
        TextButton serverButton = new TextButton("Host Network Game", skin);
        serverButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {startServerGame();
            }
        });
        serverButton.setPosition(600, 200);
        serverButton.setSize(150, 40);
        stage.addActor(serverButton);

    }

    private void openOptions(){
        gsm.pushState(GameStateManager.OPTIONSMENU);
    }

    private void startSinglePlayerGame(){
        gsm.setState(GameStateManager.PLAY);
    }

    private void startClientGame(){
        gsm.setState(GameStateManager.CLIENTCONNECT);
    }

    private void startServerGame(){
        gsm.setState(GameStateManager.SERVERCONNECT);
    }

    public void focus(){
        Gdx.input.setInputProcessor(stage);
    }

    public void update(float dt){}

    public void render(){
        Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        stage.act();
        stage.draw();
    }

    public void dispose(){
        stage.dispose();
        skin.dispose();
    }
}
