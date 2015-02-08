package com.mygdx.hexago.network;


public class MarkDeadPacket extends Packet{
    public int x;
    public int y;
    public boolean markdead; // if true, sync to dead, if false sync to alive.
}
