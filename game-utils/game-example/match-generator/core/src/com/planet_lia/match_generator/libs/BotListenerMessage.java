package com.planet_lia.match_generator.libs;

public class BotListenerMessage {
    public BotListener.MessageSender sender;
    public int botIndex;
    public Object data;

    public BotListenerMessage(BotListener.MessageSender sender, int botIndex, Object data) {
        this.sender = sender;
        this.botIndex = botIndex;
        this.data = data;
    }
}
