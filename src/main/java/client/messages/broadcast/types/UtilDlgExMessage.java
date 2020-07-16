package client.messages.broadcast.types;

import client.messages.broadcast.AbstractBroadcastMessage;
import client.messages.broadcast.BroadcastMessageType;
import util.packet.PacketWriter;

/**
 * NPC dialogue
 */
public class UtilDlgExMessage extends AbstractBroadcastMessage {

    private final String message;
    private final int npc;

    public UtilDlgExMessage(String message, int npc) {
        this.message = message;
        this.npc = npc;
    }

    @Override
    public BroadcastMessageType getType() {
        return BroadcastMessageType.UTIL_DLG_EX;
    }

    @Override
    protected void encodeData(PacketWriter pw) {
        pw.writeMapleString(message);
        pw.writeInt(npc);
    }
}
