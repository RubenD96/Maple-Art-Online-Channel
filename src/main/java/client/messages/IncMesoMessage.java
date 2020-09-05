package client.messages;

import util.packet.PacketWriter;

public class IncMesoMessage extends AbstractMessage {

    private final int meso;

    public IncMesoMessage(int meso) {
        this.meso = meso;
    }

    @Override
    public MessageType getType() {
        return MessageType.INC_MESO_MESSAGE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeInt(meso);
    }
}
