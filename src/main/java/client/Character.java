package client;

import field.life.AbstractFieldLife;
import field.life.FieldObjectType;
import lombok.Data;
import lombok.NonNull;
import net.maple.packets.FieldPackets;
import client.player.Job;
import client.player.key.KeyAction;
import client.player.key.KeyBinding;
import client.player.key.KeyType;
import util.packet.Packet;

import java.util.HashMap;
import java.util.Map;

@Data
public class Character extends AbstractFieldLife {

    /**
     * Start constructor fields
     */
    @NonNull final Client client;
    @NonNull String name;
    @NonNull int id, gmLevel, level, hair, face;
    @NonNull int gender, skinColor;
    @NonNull Job job;
    @NonNull int ap, sp, fame, fieldId, spawnpoint;
    @NonNull int strength, dexterity, intelligence, luck;
    @NonNull int health, maxHealth, mana, maxMana, exp;
    /**
     * End constructor fields
     */
    Map<Byte, Integer> equipment = new HashMap<>();
    final Pet[] pets = new Pet[3];
    final KeyBinding[] keyBindings = new KeyBinding[90];
    final int[] quickSlotKeys = new int[8];
    Integer portableChair = null;

    public void init() {
        resetKeyBindings();
    }

    public void resetKeyBindings() {
        keyBindings[2] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_ALL);
        keyBindings[3] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_PARTY);
        keyBindings[4] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_FRIEND);
        keyBindings[5] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_GUILD);
        keyBindings[6] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_ALLIANCE);
        keyBindings[7] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_COUPLE);
        keyBindings[8] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_EXPEDITION);
        keyBindings[16] = new KeyBinding(KeyType.MENU, KeyAction.QUEST);
        keyBindings[17] = new KeyBinding(KeyType.MENU, KeyAction.WORLD_MAP);
        keyBindings[18] = new KeyBinding(KeyType.MENU, KeyAction.EQUIP);
        keyBindings[19] = new KeyBinding(KeyType.MENU, KeyAction.FRIEND);
        keyBindings[20] = new KeyBinding(KeyType.MENU, KeyAction.EXPEDITION);
        keyBindings[23] = new KeyBinding(KeyType.MENU, KeyAction.ITEM);
        keyBindings[24] = new KeyBinding(KeyType.MENU, KeyAction.PARTY_SEARCH);
        keyBindings[25] = new KeyBinding(KeyType.MENU, KeyAction.PARTY);
        keyBindings[26] = new KeyBinding(KeyType.MENU, KeyAction.SHORTCUT);
        keyBindings[27] = new KeyBinding(KeyType.MENU, KeyAction.QUICK_SLOT);
        keyBindings[29] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.ATTACK);
        keyBindings[31] = new KeyBinding(KeyType.MENU, KeyAction.STAT);
        keyBindings[33] = new KeyBinding(KeyType.MENU, KeyAction.FAMILY);
        keyBindings[34] = new KeyBinding(KeyType.MENU, KeyAction.GUILD);
        keyBindings[35] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_WHISPER);
        keyBindings[37] = new KeyBinding(KeyType.MENU, KeyAction.SKILL);
        keyBindings[38] = new KeyBinding(KeyType.MENU, KeyAction.QUEST_ALARM);
        keyBindings[39] = new KeyBinding(KeyType.MENU, KeyAction.MEDAL_QUEST);
        keyBindings[40] = new KeyBinding(KeyType.MENU, KeyAction.CHAT_TYPE);
        keyBindings[41] = new KeyBinding(KeyType.MENU, KeyAction.CASH_SHOP);
        keyBindings[43] = new KeyBinding(KeyType.MENU, KeyAction.KEY_CONFIG);
        keyBindings[44] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.PICKUP);
        keyBindings[45] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.SIT);
        keyBindings[46] = new KeyBinding(KeyType.MENU, KeyAction.MESSENGER);
        keyBindings[50] = new KeyBinding(KeyType.MENU, KeyAction.MINI_MAP);
        keyBindings[56] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.JUMP);
        keyBindings[57] = new KeyBinding(KeyType.BASIC_ACTION, KeyAction.NPC_TALK);
        keyBindings[59] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_0);
        keyBindings[60] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_1);
        keyBindings[61] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_2);
        keyBindings[62] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_3);
        keyBindings[63] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_4);
        keyBindings[64] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_5);
        keyBindings[65] = new KeyBinding(KeyType.BASIC_EMOTION, KeyAction.EMOTION_6);

        int[] slots = {42, 82, 71, 73, 29, 83, 79, 81};
        System.arraycopy(slots, 0, quickSlotKeys, 0, quickSlotKeys.length);
    }

    public boolean isGM() {
        return gmLevel > 0;
    }

    public void write(Packet msg) {
        client.write(msg);
    }

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.CHARACTER;
    }

    @Override
    public Packet getEnterFieldPacket() {
        return FieldPackets.enterField(this);
    }

    @Override
    public Packet getLeaveFieldPacket() {
        return FieldPackets.leaveField(this);
    }

    @Override
    public String toString() {
        return name + ":" + id;
    }
}
