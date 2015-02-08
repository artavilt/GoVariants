package com.mygdx.hexago.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.hexago.handlers.GameStateManager;
import com.mygdx.hexago.main.Game;


public abstract class GameState {
    protected GameStateManager gsm;
    protected Game game;

    protected OrthographicCamera cam;

    protected GameState(GameStateManager gsm){
        this.gsm = gsm;
        this.game = gsm.game();
        //sb = this.game.getSpriteBatch();
        cam = this.game.getCam();
    }

    public abstract void update(float dt);
    // called when the state is returned to the top of the stack.
    public abstract void focus();
    //public abstract void handleInput();
    public abstract void resize(int width, int height);

    public abstract void render();
    public abstract void dispose();

}
