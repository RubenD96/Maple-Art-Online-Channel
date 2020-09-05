package client.messages;

import util.packet.PacketWriter;

public class MesoDropPickUpMessage extends AbstractMessage {

    private boolean failed;
    private final int meso;
    private short premiumIPMesoBonus; // wtf

    public MesoDropPickUpMessage(int meso) {
        this.meso = meso;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public int getMeso() {
        return meso;
    }

    public short getPremiumIPMesoBonus() {
        return premiumIPMesoBonus;
    }

    public void setPremiumIPMesoBonus(short premiumIPMesoBonus) {
        this.premiumIPMesoBonus = premiumIPMesoBonus;
    }

    @Override
    public MessageType getType() {
        return MessageType.DROP_PICK_UP_MESSAGE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.write(1); // meso

        pw.writeBool(failed);
        pw.writeInt(meso);
        pw.writeShort(premiumIPMesoBonus);
    }
}
