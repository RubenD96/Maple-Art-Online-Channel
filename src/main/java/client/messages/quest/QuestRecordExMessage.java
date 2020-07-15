package client.messages.quest;

import client.messages.MessageType;
import util.packet.PacketWriter;

public class QuestRecordExMessage extends AbstractQuestMessage {

    private final String value;

    public QuestRecordExMessage(short questId, String value) {
        super(questId);
        this.value = value;
    }

    @Override
    public MessageType getType() {
        return MessageType.QUEST_RECORD_EX_MESSAGE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        super.encodeData(pw);
        pw.writeMapleString(value);
    }
}
