package com.mygdx.hexago.network;

import com.mygdx.hexago.boardcore.BoardConfig;

public class BoardConfigPacket extends Packet {
    public BoardConfig boardConfig;

    public BoardConfigPacket(){
        this.boardConfig = new BoardConfig();
    }
}
