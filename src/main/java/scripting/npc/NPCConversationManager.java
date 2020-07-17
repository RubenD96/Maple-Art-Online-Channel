package scripting.npc;

import client.Client;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.maple.packets.ConversationPackets;
import scripting.AbstractPlayerInteraction;

@Getter
@SuppressWarnings("unused")
public class NPCConversationManager extends AbstractPlayerInteraction {

    protected @NonNull final int npcId;
    private @Setter String text = "";

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

    public void sendGetText(String text, String def, int min, int max) {
        c.write(ConversationPackets.getTextMessagePacket(npcId, 0, text, def, min, max));
    }

    public void sendGetText(String text, String def, int min, int max, int speaker) {
        c.write(ConversationPackets.getTextMessagePacket(npcId, speaker, text, def, min, max));
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        c.write(ConversationPackets.getNumberMessagePacket(npcId, 0, text, def, min, max));
    }

    public void sendGetNumber(String text, int def, int min, int max, int speaker) {
        c.write(ConversationPackets.getNumberMessagePacket(npcId, speaker, text, def, min, max));
    }

    public void sendSimple(String text) {
        c.write(ConversationPackets.getSimpleMessagePacket(npcId, 0, text));
    }

    public void sendSimple(String text, int speaker) {
        c.write(ConversationPackets.getSimpleMessagePacket(npcId, speaker, text));
    }

    public void sendAcceptDecline(String text) {
        c.write(ConversationPackets.getAcceptMessagePacket(npcId, 0, text));
    }

    public void sendAcceptDecline(String text, int speaker) {
        c.write(ConversationPackets.getAcceptMessagePacket(npcId, speaker, text));
    }

    public void sendGetTextBox() {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, 0, "", 48, 6));
    }

    public void sendGetTextBox(int speaker) {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, speaker, "", 48, 6));
    }

    public void sendGetTextBox(String def, int cols, int rows) {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, 0, def, cols, rows));
    }

    public void sendGetTextBox(String def, int cols, int rows, int speaker) {
        c.write(ConversationPackets.getBoxTextMessagePacket(npcId, speaker, def, cols, rows));
    }

    public void sendSlide(String text, int type, int selected) {
        c.write(ConversationPackets.getSlideMenuMessagePacket(npcId, 0, text, type, selected));
    }

    public void sendSlide(String text, int type, int selected, int speaker) {
        c.write(ConversationPackets.getSlideMenuMessagePacket(npcId, speaker, text, type, selected));
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this);
    }

    public void startQuest(int qid) {
        getPlayer().startQuest(qid, npcId);
    }

    public void completeQuest(int qid) {
        getPlayer().completeQuest(qid);
    }
}
