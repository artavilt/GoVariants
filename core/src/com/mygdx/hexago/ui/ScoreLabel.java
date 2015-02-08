package com.mygdx.hexago.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.hexago.boardcore.ScoreIO;
import com.mygdx.hexago.boardcore.TileState;

public class ScoreLabel implements ScoreIO {
    private static final String WHITEPREPEND = "White: ";
    private static final String BLACKPREPEND = "Black: ";
    Label label;
    String prepend;
    public ScoreLabel(Label label, int turn){
        this.label = label;
        prepend = turn == TileState.WHITE ? WHITEPREPEND : BLACKPREPEND;
    }

    public void setScore(int score){
        label.setText(prepend + score);
    }

}
