package client;

import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.player.Job;
import client.player.StatType;
import client.player.key.KeyBinding;
import field.object.FieldObjectType;
import field.object.life.AbstractFieldLife;
import field.object.life.FieldControlledObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.database.CharacterAPI;
import net.maple.packets.CharacterPackets;
import net.maple.packets.FieldPackets;
import net.server.ChannelServer;
import util.packet.Packet;

import java.util.*;

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
    private byte portal = -1;
    @Getter List<FieldControlledObject> controlledObjects = new ArrayList<>();
    @Getter Map<ItemInventoryType, ItemInventory> inventories = new HashMap<>();

    public void init() {
        resetQuickSlot();
        keyBindings = CharacterAPI.getKeyBindings(id);
        inventories.put(ItemInventoryType.EQUIP, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.CONSUME, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.INSTALL, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.ETC, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.CASH, new ItemInventory((short) 24));
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

    public void levelUp() {
        level++;
        maxHealth += 50; // just random number for now
        maxMana += 5;
        health = maxHealth;
        mana = maxMana;
        ap += 5;

        List<StatType> statTypes = new ArrayList<>();
        statTypes.add(StatType.LEVEL);
        statTypes.add(StatType.MAX_HP);
        statTypes.add(StatType.MAX_MP);
        statTypes.add(StatType.HP);
        statTypes.add(StatType.MP);
        statTypes.add(StatType.AP);
        updateStats(statTypes, false);
    }

    /**
     * Used for GM's to manipulate levels.
     *
     * @param level new level
     * @see #levelUp() for regular players
     */
    public void setLevel(int level) {
        this.level = level;
        updateSingleStat(StatType.LEVEL);
    }

    public void setJob(int jobId) {
        job = Job.getById(jobId);
        updateSingleStat(StatType.JOB);
    }

    public void enableActions() {
        CharacterPackets.statUpdate(this, new ArrayList<>(), true);
    }

    public void updateStats(List<StatType> statTypes, boolean enableActions) {
        CharacterPackets.statUpdate(this, statTypes, enableActions);
    }

    public ChannelServer getChannel() {
        return client.getWorldChannel();
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
