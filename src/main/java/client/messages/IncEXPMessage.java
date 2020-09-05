package client.messages;

import util.packet.PacketWriter;

public class IncEXPMessage extends AbstractMessage {

    private boolean isLastHit, onQuest;
    private int exp, expBySMQ, weddingBonusExp, partyBonusExp, itemBonusExp, premiumIPExp, rainbowWeekEventExp, partyExpRingExp, cakePieEventBonus;
    private byte mobEventBonusPercentage, partyBonusPercentage, playTimeHour, questBonusRate, questBonusRemainCount, partyBonusEventRate;

    public void setLastHit(boolean lastHit) {
        isLastHit = lastHit;
    }

    public void setOnQuest(boolean onQuest) {
        this.onQuest = onQuest;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setExpBySMQ(int expBySMQ) {
        this.expBySMQ = expBySMQ;
    }

    public void setWeddingBonusExp(int weddingBonusExp) {
        this.weddingBonusExp = weddingBonusExp;
    }

    public void setPartyBonusExp(int partyBonusExp) {
        this.partyBonusExp = partyBonusExp;
    }

    public void setItemBonusExp(int itemBonusExp) {
        this.itemBonusExp = itemBonusExp;
    }

    public void setPremiumIPExp(int premiumIPExp) {
        this.premiumIPExp = premiumIPExp;
    }

    public void setRainbowWeekEventExp(int rainbowWeekEventExp) {
        this.rainbowWeekEventExp = rainbowWeekEventExp;
    }

    public void setPartyExpRingExp(int partyExpRingExp) {
        this.partyExpRingExp = partyExpRingExp;
    }

    public void setCakePieEventBonus(int cakePieEventBonus) {
        this.cakePieEventBonus = cakePieEventBonus;
    }

    public void setMobEventBonusPercentage(byte mobEventBonusPercentage) {
        this.mobEventBonusPercentage = mobEventBonusPercentage;
    }

    public void setPartyBonusPercentage(byte partyBonusPercentage) {
        this.partyBonusPercentage = partyBonusPercentage;
    }

    public void setPlayTimeHour(byte playTimeHour) {
        this.playTimeHour = playTimeHour;
    }

    public void setQuestBonusRate(byte questBonusRate) {
        this.questBonusRate = questBonusRate;
    }

    public void setQuestBonusRemainCount(byte questBonusRemainCount) {
        this.questBonusRemainCount = questBonusRemainCount;
    }

    public void setPartyBonusEventRate(byte partyBonusEventRate) {
        this.partyBonusEventRate = partyBonusEventRate;
    }

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
