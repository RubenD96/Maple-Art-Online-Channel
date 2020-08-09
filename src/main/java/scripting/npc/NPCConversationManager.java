package scripting.npc;

import client.Client;
import field.object.drop.DropEntry;
import field.object.life.FieldMobTemplate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import managers.MobManager;
import net.database.DropAPI;
import net.maple.packets.ConversationPackets;
import scripting.AbstractPlayerInteraction;

import java.util.AbstractMap;
import java.util.List;
import java.util.function.Function;

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

    /**
     * cm.letters("Hello world"); will show Christmas letters Hello world
     *
     * @param input the text to turn into christmas :)
     * @return String with item images
     */
    public String letters(String input) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                str.append("\t");
            } else {
                str.append("#i").append(convert(input.charAt(i))).append("#");
            }
        }
        return str.toString();
    }

    private int convert(char in) {
        int upper = 3991000;
        int lower = 3991026;
        int output = Character.isUpperCase(in) ? upper : lower;
        switch (Character.toLowerCase(in)) {
            case 'a':
                output += 0;
                break;
            case 'b':
                output += 1;
                break;
            case 'c':
                output += 2;
                break;
            case 'd':
                output += 3;
                break;
            case 'e':
                output += 4;
                break;
            case 'f':
                output += 5;
                break;
            case 'g':
                output += 6;
                break;
            case 'h':
                output += 7;
                break;
            case 'i':
                output += 8;
                break;
            case 'j':
                output += 9;
                break;
            case 'k':
                output += 10;
                break;
            case 'l':
                output += 11;
                break;
            case 'm':
                output += 12;
                break;
            case 'n':
                output += 13;
                break;
            case 'o':
                output += 14;
                break;
            case 'p':
                output += 15;
                break;
            case 'q':
                output += 16;
                break;
            case 'r':
                output += 17;
                break;
            case 's':
                output += 18;
                break;
            case 't':
                output += 19;
                break;
            case 'u':
                output += 20;
                break;
            case 'v':
                output += 21;
                break;
            case 'w':
                output += 22;
                break;
            case 'x':
                output += 23;
                break;
            case 'y':
                output += 24;
                break;
            case 'z':
                output += 25;
                break;
        }
        return output;
    }

    public List<DropEntry> getMobDrops(int id) {
        FieldMobTemplate template = MobManager.getMob(id);
        if (template != null) {
            if (template.getDrops() == null) {
                template.setDrops(DropAPI.getMobDrops(template.getId()));
            }
            return template.getDrops();
        }
        return null;
    }

    public void addMobDrop(int mid, int iid, double chance) {
        addMobDrop(mid, iid, 1, 1, 0, chance);
    }

    public void addMobDrop(int mid, int iid, int min, int max, double chance) {
        addMobDrop(mid, iid, min, max, 0, chance);
    }

    public void addMobDrop(int mid, int iid, int min, int max, int questid, double chance) {
        DropAPI.addMobDrop(mid, iid, min, max, questid, chance);
    }

    public void editDropChance(int mid, int iid, double chance) {
        DropAPI.updateDropChance(mid, iid, chance);
    }

    public void removeDrop(int mid, int iid) {
        DropAPI.removeDrop(mid, iid);
    }

    public void editMinMaxChance(int mid, int iid, int min, int max, double chance) {
        DropAPI.updateMinMaxChance(mid, iid, min, max, chance);
    }
}