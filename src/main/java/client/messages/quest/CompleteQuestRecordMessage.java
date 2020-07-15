package client.messages.quest;

import client.player.quest.QuestState;
import util.packet.PacketWriter;

public class CompleteQuestRecordMessage extends AbstractQuestRecordMessage {

    private final long dateCompleted;

    public CompleteQuestRecordMessage(short questId, long dateCompleted) {
        super(questId);
        this.dateCompleted = dateCompleted;
    }

    @Override
    public QuestState getState() {
        return QuestState.COMPLETE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        super.encodeData(pw);
        pw.writeLong(dateCompleted);
    }
}
