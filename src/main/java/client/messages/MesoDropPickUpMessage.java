package client.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class MesoDropPickUpMessage extends AbstractMessage {

    private @Getter @Setter boolean failed;
    private final  @Getter int meso;
    private @Getter @Setter short premiumIPMesoBonus; // wtf

    @Override
    public MessageType getType() {
        return MessageType.DROP_PICK_UP_MESSAGE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.write(1);

        pw.writeBool(failed);
        pw.writeInt(meso);
        pw.writeShort(premiumIPMesoBonus);
    }
}
