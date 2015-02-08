package com.mygdx.hexago.boardcore;

import java.util.Stack;

public interface CoreIO {

    public void setScoring();

    public void setPlaying();

    public void setReviewing();

    public void setWhiteCaptures(int captures);

    public void setBlackCaptures(int captures);

    public void setBlackName( String name );

    public void setWhiteName( String name );

    public void setWhiteScore( int score );

    public void setBlackScore( int score );

    public void gameMessage(String entry, int turn);

    public void playerMessage(String entry, int turn);

    public String getWhiteName();

    public String getBlackName();

    public void updateTurn(int turn);

    public void calibrateTiles(Stack<TileState> changes);
}
