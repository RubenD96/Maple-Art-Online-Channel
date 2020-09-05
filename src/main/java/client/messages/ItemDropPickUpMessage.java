package client.messages;

import util.packet.PacketWriter;

public class ItemDropPickUpMessage extends AbstractMessage {

    private final int itemId;

    public ItemDropPickUpMessage(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

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
