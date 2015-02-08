package com.mygdx.hexago.network;

public class NetConfig {
    public int TCPPort;
    public int UDPPort;
    public String IPAdress;

    public NetConfig(){
        IPAdress = "127.0.0.1";
        TCPPort = 54555;
        UDPPort = 57555;
    }
}
