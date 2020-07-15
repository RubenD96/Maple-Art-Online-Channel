package client.messages.quest;

import client.player.quest.QuestState;
import util.packet.PacketWriter;

public class PerformQuestRecordMessage extends AbstractQuestRecordMessage {

    private final String value;

    public PerformQuestRecordMessage(short questId, String value) {
        super(questId);
        this.value = value;
    }

    @Override
    public QuestState getState() {
        return QuestState.PERFORM;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        super.encodeData(pw);
        pw.writeMapleString(value);
    }
}
