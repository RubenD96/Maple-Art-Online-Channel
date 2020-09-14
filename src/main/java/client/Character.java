package client;

import client.interaction.shop.NPCShop;
import client.interaction.storage.ItemStorageInteraction;
import client.inventory.ItemInventory;
import client.inventory.ItemInventoryType;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import client.inventory.slots.ItemSlotEquip;
import client.messages.IncMesoMessage;
import client.messages.broadcast.types.AlertMessage;
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
import net.database.*;
import net.maple.packets.CharacterPackets;
import net.maple.packets.FieldPackets;
import net.maple.packets.PartyPackets;
import net.server.ChannelServer;
import net.server.Server;
import scripting.npc.NPCScriptManager;
import util.packet.Packet;
import world.guild.Guild;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Character extends AbstractFieldLife {

    /**
     * Start constructor fields
     */
    final Client client;
    public String name;
    int id, gmLevel, level, hair, face;
    int gender, skinColor;
    Job job;
    int ap, sp, fame, fieldId, spawnpoint;
    int strength, dexterity, intelligence, luck;
    int health, maxHealth, mana, maxMana, exp;
    int meso;

    public Character(Client client, String name, int id, int gmLevel, int level, int hair, int face, int gender, int skinColor, Job job, int ap, int sp, int fame, int fieldId, int spawnpoint, int strength, int dexterity, int intelligence, int luck, int health, int maxHealth, int mana, int maxMana, int exp, int meso) {
        this.client = client;
        this.name = name;
        this.id = id;
        this.gmLevel = gmLevel;
        this.level = level;
        this.hair = hair;
        this.face = face;
        this.gender = gender;
        this.skinColor = skinColor;
        this.job = job;
        this.ap = ap;
        this.sp = sp;
        this.fame = fame;
        this.fieldId = fieldId;
        this.spawnpoint = spawnpoint;
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.luck = luck;
        this.health = health;
        this.maxHealth = maxHealth;
        this.mana = mana;
        this.maxMana = maxMana;
        this.exp = exp;
        this.meso = meso;
    }

    public Client getClient() {
        return client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getGmLevel() {
        return gmLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getHair() {
        return hair;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public int getFace() {
        return face;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getSkinColor() {
        return skinColor;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public int getAp() {
        return ap;
    }

    public void setAp(int ap) {
        this.ap = ap;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp = sp;
    }

    public int getFame() {
        return fame;
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getSpawnpoint() {
        return spawnpoint;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMeso() {
        return meso;
    }

    public void setMeso(int meso) {
        this.meso = meso;
    }

    public Pet[] getPets() {
        return pets;
    }

    public Map<Integer, KeyBinding> getKeyBindings() {
        return keyBindings;
    }

    public int[] getQuickSlotKeys() {
        return quickSlotKeys;
    }

    public Integer getPortableChair() {
        return portableChair;
    }

    public void setPortableChair(Integer portableChair) {
        this.portableChair = portableChair;
    }

    public byte getPortal() {
        return portal;
    }

    public void setPortal(byte portal) {
        this.portal = portal;
    }

    public List<FieldControlledObject> getControlledObjects() {
        return controlledObjects;
    }

    public Map<ItemInventoryType, ItemInventory> getInventories() {
        return inventories;
    }

    public void setInventories(Map<ItemInventoryType, ItemInventory> inventories) {
        this.inventories = inventories;
    }

    public FriendList getFriendList() {
        return friendList;
    }

    public int getTrueMaxHealth() {
        return trueMaxHealth;
    }

    public int getPhilId() {
        return philId;
    }

    public void setPhilId(int philId) {
        this.philId = philId;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public boolean isInCashShop() {
        return inCashShop;
    }

    public void setInCashShop(boolean inCashShop) {
        this.inCashShop = inCashShop;
    }

    public Map<Integer, Quest> getQuests() {
        return quests;
    }

    public void setQuests(Map<Integer, Quest> quests) {
        this.quests = quests;
    }

    public Set<Integer> getTowns() {
        return towns;
    }

    public Set<Integer> getRegisteredQuestMobs() {
        return registeredQuestMobs;
    }

    public int[] getWishlist() {
        return wishlist;
    }

    public void setWishlist(int[] wishlist) {
        this.wishlist = wishlist;
    }

    public NPCShop getNpcShop() {
        return npcShop;
    }

    public void setNpcShop(NPCShop npcShop) {
        this.npcShop = npcShop;
    }

    public ItemStorageInteraction getActiveStorage() {
        return activeStorage;
    }

    public void setActiveStorage(ItemStorageInteraction activeStorage) {
        this.activeStorage = activeStorage;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Set<String> getGuildInvitesSent() {
        return guildInvitesSent;
    }

    /**
     * End constructor fields
     */
    final Pet[] pets = new Pet[3];
    Map<Integer, KeyBinding> keyBindings = new HashMap<>();
    final int[] quickSlotKeys = new int[8];
    Integer portableChair = null;
    byte portal = -1;
    List<FieldControlledObject> controlledObjects = new ArrayList<>();
    Map<ItemInventoryType, ItemInventory> inventories = new HashMap<>();
    FriendList friendList;
    int trueMaxHealth, trueMaxMana, philId;
    Party party;
    boolean inCashShop;
    Map<Integer, Quest> quests = new HashMap<>();
    Set<Integer> towns = new TreeSet<>();
    Set<Integer> registeredQuestMobs = new HashSet<>();
    int[] wishlist = new int[10];
    NPCShop npcShop = null;
    ItemStorageInteraction activeStorage = null;
    Guild guild;
    Set<String> guildInvitesSent = new HashSet<>();

    public void init() {
        portal = (byte) spawnpoint;
        resetQuickSlot();
        keyBindings = CharacterAPI.INSTANCE.getKeyBindings(id);
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
        CharacterAPI.INSTANCE.saveCharacterStats(this);
        CharacterAPI.INSTANCE.updateKeyBindings(this);
        ItemAPI.INSTANCE.saveInventories(this);
        QuestAPI.INSTANCE.saveInfo(this);
        WishlistAPI.INSTANCE.save(this);
    }

    public boolean isGM() {
        return gmLevel > 0;
    }

    public void gainMeso(int meso) {
        gainMeso(meso, false);
    }

    public void gainMeso(int meso, boolean effect) {
        this.meso += meso;
        updateSingleStat(StatType.MESO);
        if (effect) {
            write(CharacterPackets.message(new IncMesoMessage(meso)));
        }
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
        TownsAPI.INSTANCE.add(this, id);
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
        // if you enable this, completeQuest() has to be called before taking any items, risky
        /*if (!quest.canFinish()) {
            client.close(this, "Invalid quest finish requirements (" + qid + ")");
            return;
        }*/

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
        int needed = level < 50 ? UserConstants.INSTANCE.getExpTable()[level] : 1242;
        if (this.exp >= needed) {
            this.exp -= UserConstants.INSTANCE.getExpTable()[level]; // leftover
            levelUp();
            if (exp > 0) {
                gainExp(this.exp);
            }
        }
        updateSingleStat(StatType.EXP);
    }

    public void setJob(int jobId) {
        Job job = Job.Companion.getById(jobId);
        if (job != null) {
            this.job = job;
            updateSingleStat(StatType.JOB);
        } else {
            write(CharacterPackets.message(new AlertMessage("This job does not exist!")));
        }
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
        for (Map.Entry<Short, ItemSlot> item : inventories.get(ItemInventoryType.EQUIP).getItems().entrySet()) {
            if (item.getKey() < 0) {
                ItemSlotEquip equip = (ItemSlotEquip) item.getValue();
                trueMaxHealth += equip.getMaxHP();
                trueMaxMana += equip.getMaxMP();
            }
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
        if (this.health < 0) this.health = 0;
        updateSingleStat(StatType.HP, false);
        updatePartyHP(false);
    }

    public void modifyMana(int mana) {
        this.mana += mana;
        if (this.mana < 0) this.mana = 0;
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
        } else if (this.health < 0) this.health = 0;
        this.mana += mana;
        if (this.mana > trueMaxMana) {
            this.mana = trueMaxMana;
        } else if (this.mana < 0) this.mana = 0;
        updateStats(new ArrayList<>(Arrays.asList(StatType.HP, StatType.MP)), false);
        updatePartyHP(false);
    }

    public void updatePartyHP(boolean receive) {
        if (party != null) {
            party.getMembers().forEach(m -> {
                if (m.isOnline() && m.getChannel() == getChannel().getChannelId() && m.getField() == fieldId && m.getCid() != id) {
                    Character chr = Server.Companion.getInstance().getCharacter(m.getCid());
                    if (receive) {
                        write(PartyPackets.INSTANCE.getUpdatePartyHealthPacket(chr));
                    }
                    chr.write(PartyPackets.INSTANCE.getUpdatePartyHealthPacket(this));
                }
            });
        }
    }

    public void loadParty() {
        Party party = Server.Companion.getInstance().getParties().get(CharacterAPI.INSTANCE.getOldPartyId(id));
        if (party != null) {
            PartyMember member = party.getMember(id);
            if (member != null) {
                this.party = party;
                member.setCharacter(this);
                member.loadParty(party);
            }
        }
    }

    public void loadGuild() {
        int gid = GuildAPI.INSTANCE.getGuildId(this);
        if (gid != -1) {
            Guild guild = GuildAPI.INSTANCE.load(gid);
            if (guild == null) {
                client.close(this, "Guild is null after database retrieval gid: " + gid);
                return;
            }

            guild.getMembers().get(id).setCharacter(this);
            guild.getMembers().get(id).setOnline(true);
            this.guild = guild;
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

    public boolean hasInvSpace(ItemSlot item) {
        return hasInvSpace(item.getTemplateId());
    }

    public boolean hasInvSpace(int item) {
        return hasInvSpace(ItemInventoryType.values()[(item / 1000000) - 1]);
    }

    public boolean hasInvSpace(ItemInventoryType type) {
        return getAvailableSlots(type) > 0;
    }

    public int getAvailableSlots(ItemInventoryType type) {
        ItemInventory inv = inventories.get(type);

        short used = (short) inv.getItems().keySet().stream()
                .filter(s -> s > 0)
                .filter(s -> s <= inv.getSlotMax())
                .count();

        return inv.getSlotMax() - used;
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
        NPCScriptManager.getInstance().dispose(client);
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
