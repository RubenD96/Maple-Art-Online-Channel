package client;

import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import client.inventory.slots.ItemSlotEquip;
import client.messages.quest.CompleteQuestRecordMessage;
import client.messages.quest.PerformQuestRecordMessage;
import client.messages.quest.ResignQuestRecordMessage;
import client.party.Party;
import client.party.PartyMember;
import client.player.Job;
import client.player.StatType;
import client.player.friend.FriendList;
import client.player.key.KeyBinding;
import client.player.quest.Quest;
import constants.UserConstants;
import field.Field;
import field.object.FieldObjectType;
import field.object.life.AbstractFieldLife;
import field.object.life.FieldControlledObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.database.CharacterAPI;
import net.database.ItemAPI;
import net.database.QuestAPI;
import net.database.TownsAPI;
import net.maple.packets.CharacterPackets;
import net.maple.packets.FieldPackets;
import net.maple.packets.PartyPackets;
import net.server.ChannelServer;
import net.server.Server;
import util.packet.Packet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    int trueMaxHealth, trueMaxMana;
    Party party;
    Map<Integer, Quest> quests = new HashMap<>();
    Set<Integer> towns = new TreeSet<>();
    Set<Integer> registeredQuestMobs = new HashSet<>();

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
        QuestAPI.saveInfo(this);
    }

    public boolean isGM() {
        return gmLevel > 0;
    }

    public void gainMeso(int meso) {
        this.meso += meso;
        updateSingleStat(StatType.MESO);
    }

    public void changeField(int id) {
        changeField(id, (byte) 0);
    }

    public void changeField(int id, String portal) {
        Field field = getChannel().getFieldManager().getField(id);
        field.enter(this, portal);
    }

    public void changeField(int id, int portal) {
        Field field = getChannel().getFieldManager().getField(id);
        field.enter(this, (byte) portal);
    }

    public void addTown(int id) {
        TownsAPI.add(this, id);
        towns.add(id);
    }

    public boolean isTownUnlocked(int id) {
        return towns.contains(id);
    }

    public void forfeitQuest(int qid) {
        Quest quest = quests.get(qid);
        if (quest == null) {
            return;
        }
        quests.remove(qid);

        quest.updateState(new ResignQuestRecordMessage((short) qid, false));
    }

    public void startQuest(int qid, int npcId) {
        if (quests.containsKey(qid)) {
            return;
        }
        Quest quest = new Quest(qid, this);
        if (!quest.canStart()) {
            client.close(this, "Invalid quest start requirements (" + qid + ")");
            return;
        }
        quest.initializeMobs();
        quests.put(qid, quest);

        quest.updateState(new PerformQuestRecordMessage((short) qid, ""));
        write(quest.startQuestPacket(npcId));
    }

    public void completeQuest(int qid) {
        Quest quest = quests.get(qid);
        if (quest == null) {
            return;
        }
        if (!quest.canFinish()) {
            client.close(this, "Invalid quest finish requirements (" + qid + ")");
            return;
        }

        quest.updateState(new CompleteQuestRecordMessage((short) qid, System.currentTimeMillis()));
    }

    public void levelUp() {
        level++;
        maxHealth += 50; // just random number for now
        maxMana += 5;
        ap += 5;
        setTrueMaxStats();
        heal();

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

    public void setTrueMaxStats() {
        trueMaxHealth = maxHealth;
        trueMaxMana = maxMana;
        for (ItemSlot item : inventories.get(ItemInventoryType.EQUIP).getItems().values()) {
            ItemSlotEquip equip = (ItemSlotEquip) item;
            trueMaxHealth += equip.getMaxHP();
            trueMaxMana += equip.getMaxMP();
        }
    }

    public void setHealth(int health) {
        if (health > trueMaxHealth) {
            health = trueMaxHealth;
        }
        this.health = health;
        updateSingleStat(StatType.HP, false);
        updatePartyHP(false);
    }

    public void setMana(int mana) {
        if (mana > trueMaxMana) {
            mana = trueMaxMana;
        }
        this.mana = mana;
        updateSingleStat(StatType.MP, false);
    }

    /**
     * Only use for reduction of hp
     *
     * @param health reduction of hp
     */
    public void modifyHealth(int health) {
        this.health += health;
        updateSingleStat(StatType.HP, false);
        updatePartyHP(false);
    }

    public void modifyMana(int mana) {
        this.mana += mana;
        updateSingleStat(StatType.MP, false);
    }

    public void heal() {
        heal(true);
    }

    public void heal(boolean update) {
        health = trueMaxHealth;
        mana = trueMaxMana;
        if (update)
            updateStats(new ArrayList<>(Arrays.asList(StatType.HP, StatType.MP)), false);
    }

    public void modifyHPMP(int health, int mana) {
        this.health += health;
        if (this.health > trueMaxHealth) {
            this.health = trueMaxHealth;
        }
        this.mana += mana;
        if (this.mana > trueMaxMana) {
            this.mana = trueMaxMana;
        }
        updateStats(new ArrayList<>(Arrays.asList(StatType.HP, StatType.MP)), false);
        updatePartyHP(false);
    }

    public void updatePartyHP(boolean receive) {
        if (party != null) {
            party.getMembers().forEach(m -> {
                if (m.isOnline() && m.getChannel() == getChannel().getChannelId() && m.getField() == fieldId && m.getCid() != id) {
                    Character chr = Server.getInstance().getCharacter(m.getCid());
                    if (receive) {
                        write(PartyPackets.getUpdatePartyHealthPacket(chr));
                    }
                    chr.write(PartyPackets.getUpdatePartyHealthPacket(this));
                }
            });
        }
    }

    public void loadParty() {
        Party party = Server.getInstance().getParties().get(CharacterAPI.getOldPartyId(id));
        if (party != null) {
            PartyMember member = party.getMember(id);
            if (member != null) {
                this.party = party;
                member.setCharacter(this);
                member.loadParty(party);
            }
        }
    }

    public void validateStats() {
        setTrueMaxStats();
        if (health > trueMaxHealth) setHealth(trueMaxHealth);
        if (mana > trueMaxMana) setMana(trueMaxMana);
    }

    public int getItemQuantity(int item) {
        ItemInventoryType type = ItemInventoryType.values()[(item / 1000000) - 1];

        AtomicInteger quantity = new AtomicInteger(0);
        inventories.get(type).getItems().values().stream()
                .filter(itemSlot -> itemSlot.getTemplateId() == item)
                .forEach(itemSlot -> {
                    if (itemSlot instanceof ItemSlotBundle) {
                        quantity.addAndGet(((ItemSlotBundle) itemSlot).getNumber());
                    } else {
                        quantity.incrementAndGet();
                    }
                });
        return quantity.get();
    }

    public void incStrength() {
        strength++;
    }

    public void incDexterity() {
        dexterity++;
    }

    public void incLuck() {
        luck++;
    }

    public void incIntelligence() {
        intelligence++;
    }

    public void decAP() {
        ap--;
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
