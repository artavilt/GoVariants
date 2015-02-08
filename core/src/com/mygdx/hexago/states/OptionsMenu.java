package com.mygdx.hexago.states;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hexago.boardcore.BoardConfig;
import com.mygdx.hexago.handlers.GameStateManager;


public class OptionsMenu extends MenuState{
    private BoardConfig boardConfig;
    private BoardConfig oldBoardConfig;
    private SelectBox<Integer> boardRadiusSelect;

    public OptionsMenu(GameStateManager gsm, BoardConfig boardConfig){
        super(gsm);
        this.boardConfig = boardConfig;
        this.oldBoardConfig = boardConfig.copy();

        // Set Radius
        // label
        Label boardRadiusLabel = new Label("Set Board Radius", skin);
        boardRadiusLabel.setPosition(50, 300);
        stage.addActor( boardRadiusLabel );
        // select box
        boardRadiusSelect = new SelectBox<Integer>(skin);
        boardRadiusSelect.setItems(new Array<Integer>(BoardConfig.RADIUSCHOICES));
        boardRadiusSelect.setSelected(boardConfig.boardRadius);
        boardRadiusSelect.setPosition(175, 300);
        boardRadiusSelect.setSize(50, 30);
        boardRadiusSelect.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                setBoardRadius();
            }
        });
        stage.addActor( boardRadiusSelect );


        TextButton done = new TextButton("Done", skin);
        done.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                finish();
            }
        });
        done.setPosition(300, 50);
        done.setSize(100, 40);
        stage.addActor(done);
    }

    private void setBoardRadius(){
        boardConfig.boardRadius = boardRadiusSelect.getSelected();
    }

    private void finish(){
        gsm.popState();
    }


}
