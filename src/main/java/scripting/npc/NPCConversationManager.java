package scripting.npc;

import client.Client;
import lombok.Getter;
import lombok.NonNull;
import net.maple.packets.ConversationPackets;

@Getter
@SuppressWarnings("unused")
public class NPCConversationManager extends AbstractPlayerInteraction {

    @NonNull final int npcId;

    public NPCConversationManager(@NonNull Client c, int npcId) {
        super(c);
        this.npcId = npcId;
    }

    public void sendOk(String text) {
        c.write(ConversationPackets.getOkMessagePacket(npcId, 0, text));
    }

    public void sendOk(String text, int speaker) {
        c.write(ConversationPackets.getOkMessagePacket(npcId, speaker, text));
    }

    public void sendNext(String text) {
        c.write(ConversationPackets.getNextMessagePacket(npcId, 0, text));
    }

    public void sendNext(String text, int speaker) {
        c.write(ConversationPackets.getNextMessagePacket(npcId, speaker, text));
    }

    public void sendPrev(String text) {
        c.write(ConversationPackets.getPrevMessagePacket(npcId, 0, text));
    }

    public void sendPrev(String text, int speaker) {
        c.write(ConversationPackets.getPrevMessagePacket(npcId, speaker, text));
    }

    public void sendNextPrev(String text) {
        c.write(ConversationPackets.getNextPrevMessagePacket(npcId, 0, text));
    }

    public void sendNextPrev(String text, int speaker) {
        c.write(ConversationPackets.getNextPrevMessagePacket(npcId, speaker, text));
    }

    public void sendYesNo(String text) {
        c.write(ConversationPackets.getYesNoMessagePacket(npcId, 0, text));
    }

    public void sendYesNo(String text, int speaker) {
        c.write(ConversationPackets.getYesNoMessagePacket(npcId, speaker, text));
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this);
    }
}
