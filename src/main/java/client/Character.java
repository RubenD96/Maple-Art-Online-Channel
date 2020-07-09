package client;

import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.player.Job;
import client.player.StatType;
import client.player.friend.FriendList;
import client.player.key.KeyBinding;
import constants.UserConstants;
import field.object.FieldObjectType;
import field.object.life.AbstractFieldLife;
import field.object.life.FieldControlledObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.database.CharacterAPI;
import net.database.ItemAPI;
import net.maple.packets.CharacterPackets;
import net.maple.packets.FieldPackets;
import net.server.ChannelServer;
import util.packet.Packet;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
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
    final Pet[] pets = new Pet[3];
    Map<Integer, KeyBinding> keyBindings = new HashMap<>();
    final int[] quickSlotKeys = new int[8];
    Integer portableChair = null;
    private byte portal = -1;
    List<FieldControlledObject> controlledObjects = new ArrayList<>();
    Map<ItemInventoryType, ItemInventory> inventories = new HashMap<>();
    FriendList friendList;

    public void init() {
        resetQuickSlot();
        keyBindings = CharacterAPI.getKeyBindings(id);
        inventories.put(ItemInventoryType.EQUIP, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.CONSUME, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.INSTALL, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.ETC, new ItemInventory((short) 24));
        inventories.put(ItemInventoryType.CASH, new ItemInventory((short) 96));

        friendList = new FriendList(this);
    }

    public void resetQuickSlot() {
        int[] slots = {42, 82, 71, 73, 29, 83, 79, 81};
        System.arraycopy(slots, 0, quickSlotKeys, 0, quickSlotKeys.length);
    }

    public void save() {
        CharacterAPI.saveCharacterStats(this);
        CharacterAPI.updateKeyBindings(this);
        ItemAPI.saveInventories(this);
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

    public void gainExp(int exp) {
        if (level >= UserConstants.maxLevel) return;

        this.exp += exp;
        int needed = level < 50 ? UserConstants.expTable[level] : 1242;
        if (this.exp >= needed) {
            this.exp -= UserConstants.expTable[level]; // leftover
            levelUp();
            if (exp > 0) {
                gainExp(this.exp);
            }
        }
        updateSingleStat(StatType.EXP);
    }

    public void setJob(int jobId) {
        job = Job.getById(jobId);
        updateSingleStat(StatType.JOB);
    }

    public void fame() {
        fame++;
        updateSingleStat(StatType.FAME, false);
    }

    public void defame() {
        fame--;
        updateSingleStat(StatType.FAME, false);
    }

    public void setHealth(int health) {
        if (health > maxHealth) {
            health = maxHealth;
        }
        this.health = health;
        updateSingleStat(StatType.HP, false);
    }

    public void setMana(int mana) {
        if (mana > maxMana) {
            mana = maxMana;
        }
        this.mana = mana;
        updateSingleStat(StatType.MP, false);
    }

    public void modifyHealth(int health) {
        this.health += health;
        updateSingleStat(StatType.HP, false);
    }

    public void modifyMana(int mana) {
        this.mana += mana;
        updateSingleStat(StatType.MP, false);
    }

    public void modifyHPMP(int health, int mana) {
        this.health += health;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
        this.mana += mana;
        if (this.mana > maxMana) {
            this.mana = maxMana;
        }
        updateStats(new ArrayList<>(Arrays.asList(StatType.HP, StatType.MP)), false);
    }

    public void validateStats() {
        if (health > maxHealth) setHealth(maxHealth);
        if (mana > maxMana) setMana(maxMana);
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
        updateSingleStat(statType, true);
    }

    public void updateSingleStat(StatType statType, boolean enableActions) {
        updateStats(Collections.singletonList(statType), enableActions);
    }

    public void write(Packet msg) {
        client.write(msg);
    }

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.CHARACTER;
    }

    public void removeMe() {
        field.removeObject(this);
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
