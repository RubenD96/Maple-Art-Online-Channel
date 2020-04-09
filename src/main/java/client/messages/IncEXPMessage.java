package client.messages;

import lombok.Setter;
import util.packet.PacketWriter;

@Setter
public class IncEXPMessage extends AbstractMessage {

    private boolean isLastHit, onQuest;
    private int exp, expBySMQ, weddingBonusExp, partyBonusExp, itemBonusExp, premiumIPExp, rainbowWeekEventExp, partyExpRingExp, cakePieEventBonus;
    private byte mobEventBonusPercentage, partyBonusPercentage, playTimeHour, questBonusRate, questBonusRemainCount, partyBonusEventRate;

    @Override
    public MessageType getType() {
        return MessageType.INC_EXP_MESSAGE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeBool(isLastHit);
        pw.writeInt(exp);
        pw.writeBool(onQuest);
        pw.writeInt(expBySMQ);
        pw.write(mobEventBonusPercentage);
        pw.write(partyBonusPercentage);
        pw.writeInt(weddingBonusExp);

        if (mobEventBonusPercentage > 0) {
            pw.write(playTimeHour);
        }

        if (onQuest) {
            pw.write(questBonusRate);
            if (questBonusRate > 0) {
                pw.write(questBonusRemainCount);
            }
        }

        pw.writeByte(partyBonusEventRate);
        pw.writeInt(partyBonusExp);
        pw.writeInt(itemBonusExp);
        pw.writeInt(premiumIPExp);
        pw.writeInt(rainbowWeekEventExp);
        pw.writeInt(partyExpRingExp);
        pw.writeInt(cakePieEventBonus);
    }
}
