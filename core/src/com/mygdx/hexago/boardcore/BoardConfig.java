package com.mygdx.hexago.boardcore;


public class BoardConfig {

    public static final int CLIENT = 0;
    public static final int HOST = 1;
    public static final Integer[] RADIUSCHOICES = {5, 7, 10, 12};

    public int boardRadius;
    public int firstTurn;
    public String blackName;
    public String whiteName;

    public BoardConfig(){
        this.firstTurn = CLIENT;
        this.boardRadius = 7;
    }

    public BoardConfig copy(){
        BoardConfig b = new BoardConfig();
        b.boardRadius = this.boardRadius;
        b.firstTurn = firstTurn;
        return b;
    }

    public void setRadius(int radius){
        this.boardRadius = radius;
    }
}
