package com.mygdx.hexago.boardcore;

public interface LogIO {


    public void setBlackName( String name );

    public void setWhiteName( String name );

    public void gameMessage(String entry, int turn);

    public void playerMessage(String entry, int turn);

    public String getWhiteName();

    public String getBlackName();
}
