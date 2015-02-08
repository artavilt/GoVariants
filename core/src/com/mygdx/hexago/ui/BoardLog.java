package com.mygdx.hexago.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.mygdx.hexago.boardcore.LogIO;
import com.mygdx.hexago.boardcore.TileState;


public class BoardLog extends ScrollPane implements LogIO {
    private String whiteName;
    private String blackName;
    private Table innerTable;


    public BoardLog( Skin skin ){
        super(new Table(skin), skin);

        this.innerTable = (Table) this.getWidget();
        innerTable.defaults().expandX().fillX();
        innerTable.align(Align.left);
        innerTable.top();
        whiteName = "White";
        blackName = "Black";

        this.layout();
    }

    public void setBlackName( String name ){
        blackName = name;
    }

    public void setWhiteName( String name ){
        whiteName = name;
    }

    public void gameMessage(String entry, int turn){
        String name;
        if( turn == TileState.BLACK ){ name = blackName; }
        else{ name = whiteName; }
        updateLog( name + ":  " + entry );
    }

    public void playerMessage(String entry, int turn){
        String name;
        if( turn == TileState.BLACK ){ name = blackName; }
        else{ name = whiteName; }
        updateLog( name + ":  " + entry );
    }

    private void updateLog(String entry){
        innerTable.add(entry).align(Align.left).row();
        this.setScrollPercentY(1);
    }

}
