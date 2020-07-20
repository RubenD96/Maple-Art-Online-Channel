package client.messages;

import lombok.RequiredArgsConstructor;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class IncMesoMessage extends AbstractMessage {

    private final int meso;

    @Override
    public MessageType getType() {
        return MessageType.INC_MESO_MESSAGE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeInt(meso);
    }
}
