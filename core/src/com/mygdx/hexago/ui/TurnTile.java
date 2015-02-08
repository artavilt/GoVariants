package com.mygdx.hexago.ui;

import com.mygdx.hexago.boardcore.TurnIO;

public class TurnTile implements TurnIO {
    Tile tile;
    public TurnTile(Tile tile){
        this.tile = tile;
    }

    public void updateTurn(int turn){
        tile.update(turn);
    }
}
