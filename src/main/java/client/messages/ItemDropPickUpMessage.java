package client.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import util.packet.PacketWriter;

@RequiredArgsConstructor
public class ItemDropPickUpMessage extends AbstractMessage {

    private final @Getter int itemId;

    @Override
    public MessageType getType() {
        return MessageType.DROP_PICK_UP_MESSAGE;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.write(2); // item

        pw.writeInt(itemId);
    }
}
