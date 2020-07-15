package client.messages.quest;

import client.messages.AbstractMessage;
import util.packet.PacketWriter;

public abstract class AbstractQuestMessage extends AbstractMessage {

    private final short questId;

    protected AbstractQuestMessage(short questId) {
        this.questId = questId;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeShort(questId);
    }
}
