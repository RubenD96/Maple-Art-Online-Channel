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

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this);
    }
}
