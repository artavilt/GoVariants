package com.mygdx.hexago.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.hexago.boardcore.CaptureIO;
import com.mygdx.hexago.boardcore.TileState;

public class CaptureLabel implements CaptureIO {
    private static final String WHITEPREPEND = "White: ";
    private static final String BLACKPREPEND = "Black: ";
    Label label;
    String prepend;
    public CaptureLabel(Label label, int turn){
        this.label = label;
        prepend = turn == TileState.WHITE ? WHITEPREPEND : BLACKPREPEND;
    }

    public void setCaptures(int captures){
        label.setText(prepend + captures);
    }

}
