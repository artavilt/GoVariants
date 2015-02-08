package com.mygdx.hexago.network;


public class ChatPacket extends Packet {
    public String message;

    public ChatPacket(String message){
        this.message = message;
    }
}
