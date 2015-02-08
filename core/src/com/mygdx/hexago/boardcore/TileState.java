package com.mygdx.hexago.boardcore;


public class TileState {
    public static final int BASE = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;
    public static final int BLACKDEAD = 3;
    public static final int WHITEDEAD = 4;
    public static final int BLACKSCORE = 5;
    public static final int WHITESCORE = 6;
    public static final int NULL = 9;

    public int x;
    public int y;
    public int state;
    public Integer group;
    public Integer scoregroup;

    public TileState(int x, int y, int state){
        this.x = x;
        this.y = y;
        this.state = state;
        this.group = 0;
        this.scoregroup = 0;
    }

    public boolean isWhite(){
        return ( state == WHITE || state == WHITEDEAD );
    }

    public boolean isDead(){
        return ( state == WHITEDEAD || state == BLACKDEAD );
    }

    public int toggleDeadState( int state ){
        if( state == WHITE ){ return WHITEDEAD; }
        else if( state == BLACK ){ return BLACKDEAD; }
        else if( state == WHITEDEAD ){ return WHITE; }
        else if( state == BLACKDEAD ){ return BLACK; }
        else return BASE;
    }
}
