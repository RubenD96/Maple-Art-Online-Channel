package client.messages.quest;

import client.messages.MessageType;
import client.player.quest.QuestState;
import util.packet.PacketWriter;

public abstract class AbstractQuestRecordMessage extends AbstractQuestMessage {

    protected AbstractQuestRecordMessage(short questId) {
        super(questId);
    }

    @Override
    public MessageType getType() {
        return MessageType.QUEST_RECORD_MESSAGE;
    }

    public abstract QuestState getState();

    @Override
    protected void encodeData(PacketWriter pw) {
        super.encodeData(pw);
        pw.write(getState().getValue());
    }
}
