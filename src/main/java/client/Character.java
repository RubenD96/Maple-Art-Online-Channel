package client;

import client.player.StatType;
import field.object.FieldObjectType;
import field.object.life.AbstractFieldLife;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import net.database.CharacterAPI;
import net.maple.packets.CharacterPackets;
import net.maple.packets.FieldPackets;
import client.player.Job;
import client.player.key.KeyAction;
import client.player.key.KeyBinding;
import client.player.key.KeyType;
import util.packet.Packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
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
    @NonNull int meso;
    /**
     * End constructor fields
     */
    Map<Byte, Integer> equipment = new HashMap<>();
    final Pet[] pets = new Pet[3];
    Map<Integer, KeyBinding> keyBindings = new HashMap<>();
    final int[] quickSlotKeys = new int[8];
    Integer portableChair = null;

    public void init() {
        resetQuickSlot();
        keyBindings = CharacterAPI.getKeyBindings(id);
    }

    public void resetQuickSlot() {
        int[] slots = {42, 82, 71, 73, 29, 83, 79, 81};
        System.arraycopy(slots, 0, quickSlotKeys, 0, quickSlotKeys.length);
    }

    public void save() {
        CharacterAPI.saveCharacterStats(this);
        CharacterAPI.updateKeyBindings(this);
    }

    public boolean isGM() {
        return gmLevel > 0;
    }

    public void gainMeso(int meso) {
        this.meso += meso;
        updateSingleStat(StatType.MESO);
    }

    public void updateStats(List<StatType> statTypes, boolean enableActions) {
        CharacterPackets.statUpdate(this, statTypes, enableActions);
    }

    public void updateStats(List<StatType> statTypes) {
        updateStats(statTypes, true);
    }

    public void updateSingleStat(StatType statType) {
        updateStats(Collections.singletonList(statType));
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
