package com.mygdx.hexago.states;


import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.hexago.handlers.GameStateManager;




public class Play extends PlayState {

    public Play(GameStateManager gsm){
        super(gsm);
    }

    public void initialize(){
        chatField.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent e, int keyCode) {
                if( keyCode == Input.Keys.ENTER ){
                    boardLog.playerMessage(chatField.getText(), boardCore.getTurn());
                    chatField.setText("");
                }
                return false;
            }
        });

        playUndoButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boardCore.undo();
            }
        });
        scoreUndoButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boardCore.undo();
            }
        });
        passButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                boardCore.pass();
            }
        });
    }

    public void turnEvent(int x, int y){}
}
