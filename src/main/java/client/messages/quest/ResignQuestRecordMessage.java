package client.messages.quest;

import client.player.quest.QuestState;
import util.packet.PacketWriter;

public class ResignQuestRecordMessage extends AbstractQuestRecordMessage {

    private final boolean completed;

    public ResignQuestRecordMessage(short questId, boolean completed) {
        super(questId);
        this.completed = completed;
    }

    @Override
    public QuestState getState() {
        return QuestState.NONE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        super.encodeData(pw);
        pw.writeBool(completed);
    }
}
