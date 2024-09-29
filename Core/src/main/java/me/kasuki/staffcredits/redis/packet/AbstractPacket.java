package me.kasuki.staffcredits.redis.packet;

public abstract class AbstractPacket {

    public abstract void onSend();
    public abstract void onReceive();

}