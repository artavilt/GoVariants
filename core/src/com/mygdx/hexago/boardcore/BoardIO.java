package com.mygdx.hexago.boardcore;

import java.util.Stack;

public interface BoardIO {

    /** Updates the tile output to match the state of the boardActor */
    public void calibrateTiles(Stack<TileState> changes);

    /** attempt to take a turn */
    public void attemptPlay(int x, int y);

}
