package com.mygdx.hexago.network;

import java.io.Serializable;


public class BoardPacket extends Packet implements Serializable {
    public int y = 0;
    public int x = 0;

    public BoardPacket(){
        //this.boardState = boardState;
        //name = "Howdee";
    }
}
