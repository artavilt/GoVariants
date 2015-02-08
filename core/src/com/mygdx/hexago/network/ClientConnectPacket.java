package com.mygdx.hexago.network;


public class ClientConnectPacket extends Packet{
    public String clientName;

    public ClientConnectPacket(){
        this.clientName = "Client";
    }

}
