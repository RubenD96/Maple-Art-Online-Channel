package client

import client.interaction.shop.NPCShop
import client.interaction.storage.ItemStorageInteraction
import client.inventory.ItemInventory
import client.inventory.ItemInventoryType
import client.inventory.item.slots.ItemSlot
import client.inventory.item.slots.ItemSlotBundle
import client.inventory.item.slots.ItemSlotEquip
import client.messages.IncMesoMessage
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import client.messages.quest.CompleteQuestRecordMessage
import client.messages.quest.PerformQuestRecordMessage
import client.messages.quest.ResignQuestRecordMessage
import client.party.Party
import client.player.Job
import client.player.StatType
import client.player.friend.FriendList
import client.player.key.KeyBinding
import client.player.quest.Quest
import client.replay.MoveCollection
import constants.UserConstants
import constants.UserConstants.expTable
import database.jooq.Tables
import field.Field
import field.obj.life.FieldControlledObject
import field.obj.life.FieldMobTemplate
import kotlinx.coroutines.*
import net.database.CharacterAPI.getKeyBindings
import net.database.CharacterAPI.getMobKills
import net.database.CharacterAPI.getOldPartyId
import net.database.CharacterAPI.saveCharacterStats
import net.database.CharacterAPI.saveMobKills
import net.database.CharacterAPI.updateKeyBindings
import net.database.GuildAPI.getGuildId
import net.database.GuildAPI.load
import net.database.ItemAPI.saveInventories
import net.database.QuestAPI.saveInfo
import net.database.TownsAPI.add
import net.database.WishlistAPI.save
import net.maple.packets.CharacterPackets.message
import net.maple.packets.CharacterPackets.statUpdate
import net.maple.packets.PartyPackets.getUpdatePartyHealthPacket
import net.server.ChannelServer
import net.server.Server.getCharacter
import net.server.Server.parties
import org.jooq.Record
import scripting.npc.NPCScriptManager.dispose
import util.logging.LogType
import util.logging.Logger.log
import util.packet.Packet
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.min

class Character(val client: Client, override var name: String, val record: Record) : Avatar() {

    override var id: Int = record.getValue(Tables.CHARACTERS.ID)
    var gmLevel: Int = record.getValue(Tables.CHARACTERS.GM_LEVEL)
    override var level: Int = record.getValue(Tables.CHARACTERS.LEVEL)
        set(value) {
            field = value
            updateSingleStat(StatType.LEVEL)
        }
    override var face: Int = record.getValue(Tables.CHARACTERS.FACE)
    override var hair: Int = record.getValue(Tables.CHARACTERS.HAIR)
    override var gender: Int = record.getValue(Tables.CHARACTERS.GENDER)
    override var skinColor: Int = record.getValue(Tables.CHARACTERS.SKIN)
    override var job: Job = Job.getById(record.getValue(Tables.CHARACTERS.JOB))
        set(value) {
            field = value
            updateSingleStat(StatType.JOB)
        }
    var ap: Int = record.getValue(Tables.CHARACTERS.AP)
    var sp: Int = record.getValue(Tables.CHARACTERS.SP)
    var fame: Int = record.getValue(Tables.CHARACTERS.FAME)
    var fieldId: Int = record.getValue(Tables.CHARACTERS.MAP)
    var spawnpoint: Int = record.getValue(Tables.CHARACTERS.SPAWNPOINT)

    var strength: Int = record.getValue(Tables.CHARACTERS.STR)
    var dexterity: Int = record.getValue(Tables.CHARACTERS.DEX)
    var intelligence: Int = record.getValue(Tables.CHARACTERS.INT)
    var luck: Int = record.getValue(Tables.CHARACTERS.LUK)

    var health: Int = record.getValue(Tables.CHARACTERS.HP)
        set(value) {
            field = min(value, trueMaxHealth)
            updateSingleStat(StatType.HP, false)
            updatePartyHP(false)
        }
    var maxHealth: Int = record.getValue(Tables.CHARACTERS.MAX_HP)
    var mana: Int = record.getValue(Tables.CHARACTERS.MP)
        set(value) {
            field = min(value, trueMaxMana)
            updateSingleStat(StatType.MP, false)
        }
    var maxMana: Int = record.getValue(Tables.CHARACTERS.MAX_MP)

    var exp: Int = record.getValue(Tables.CHARACTERS.EXP)
    var meso: Int = record.getValue(Tables.CHARACTERS.MESO)
    var totalDamage: Long = record.getValue(Tables.CHARACTERS.TOTAL_DAMAGE)

    /**
     * End constructor fields
     */
    var portal: Byte = -1
    var friendList: FriendList
    var trueMaxHealth = 0
    var trueMaxMana = 0
    var philId = 0
    var isInCashShop = false

    /**
     * Collections
     */
    override val inventories: Map<ItemInventoryType, ItemInventory> = mapOf(
            ItemInventoryType.EQUIP to ItemInventory(24.toShort()),
            ItemInventoryType.CONSUME to ItemInventory(24.toShort()),
            ItemInventoryType.INSTALL to ItemInventory(24.toShort()),
            ItemInventoryType.ETC to ItemInventory(24.toShort()),
            ItemInventoryType.CASH to ItemInventory(24.toShort())
    )
    val quickSlotKeys = IntArray(8)
    val controlledObjects: MutableList<FieldControlledObject> = ArrayList()
    val quests: MutableMap<Int, Quest> = HashMap()
    val towns: MutableSet<Int> = TreeSet()
    val registeredQuestMobs: MutableSet<Int> = HashSet()
    val guildInvitesSent: MutableSet<String> = HashSet()
    val moveCollections = HashMap<Int, MoveCollection>()
    val coroutines = CoroutineCollection()
    val mobKills: MutableMap<Int, Int> = HashMap()

    /**
     * Session updates
     */
    val killedMobs: MutableSet<Int> = HashSet()

    var keyBindings: MutableMap<Int, KeyBinding> = HashMap()
    var wishlist = IntArray(10)

    /**
     * nullables
     */
    var party: Party? = null
    var npcShop: NPCShop? = null
    var activeStorage: ItemStorageInteraction? = null

    init {
        portal = spawnpoint.toByte()
        resetQuickSlot()
        keyBindings = getKeyBindings(id)
        friendList = FriendList(this)
    }

    private fun resetQuickSlot() {
        val slots = intArrayOf(42, 82, 71, 73, 29, 83, 79, 81)
        System.arraycopy(slots, 0, quickSlotKeys, 0, quickSlotKeys.size)
    }

    fun save() {
        saveCharacterStats(this)
        updateKeyBindings(this)
        saveMobKills(this)
        saveInventories(this)
        saveInfo(this)
        save(this)
    }

    val isGM: Boolean
        get() = gmLevel > 0

    fun gainMeso(meso: Int, effect: Boolean = false) {
        this.meso += meso
        updateSingleStat(StatType.MESO)
        if (effect) {
            message(IncMesoMessage(meso))
        }
    }

    fun getChannel(): ChannelServer {
        return client.worldChannel
    }

    fun changeField(id: Int, portal: String) {
        val field: Field = getChannel().fieldManager.getField(id)
        field.enter(this, portal)
    }

    fun changeField(id: Int, portal: Int = 0) {
        val field: Field = getChannel().fieldManager.getField(id)
        field.enter(this, portal.toByte())
    }

    fun addTown(id: Int) {
        add(this, id)
        towns.add(id)
    }

    fun isTownUnlocked(id: Int): Boolean {
        return towns.contains(id)
    }

    fun forfeitQuest(qid: Int) {
        val quest = quests[qid] ?: return
        quests.remove(qid)
        quest.updateState(ResignQuestRecordMessage(qid.toShort(), false))
    }

    fun startQuest(qid: Int, npcId: Int) {
        if (quests.containsKey(qid)) return

        val quest = Quest(qid, this)
        if (!quest.canStart()) {
            client.close(this, "Invalid quest start requirements ($qid)")
            return
        }

        quest.initializeMobs()
        quests[qid] = quest
        quest.updateState(PerformQuestRecordMessage(qid.toShort(), ""))
        write(quest.startQuestPacket(npcId))
    }

    fun completeQuest(qid: Int) {
        val quest = quests[qid] ?: return
        // if you enable this, completeQuest() has to be called before taking any items, risky
        /*if (!quest.canFinish()) {
            client.close(this, "Invalid quest finish requirements (" + qid + ")");
            return;
        }*/
        quest.updateState(CompleteQuestRecordMessage(qid.toShort(), System.currentTimeMillis()))
    }

    fun levelUp() {
        level++
        maxHealth += 50 // just random number for now
        maxMana += 5
        ap += 5
        setTrueMaxStats()
        heal()
        val statTypes: MutableList<StatType> = ArrayList()
        statTypes.add(StatType.LEVEL)
        statTypes.add(StatType.MAX_HP)
        statTypes.add(StatType.MAX_MP)
        statTypes.add(StatType.HP)
        statTypes.add(StatType.MP)
        statTypes.add(StatType.AP)
        updateStats(statTypes, false)
    }

    fun gainExp(exp: Int) {
        if (level >= UserConstants.maxLevel) return
        this.exp += exp
        val needed = if (level < 50) expTable[level] else 1242
        if (this.exp >= needed) {
            this.exp -= expTable[level] // leftover
            levelUp()
            if (exp > 0) {
                gainExp(this.exp)
            }
        }
        updateSingleStat(StatType.EXP)
    }

    fun setJob(jobId: Int) {
        this.job = Job.getById(jobId)
    }

    fun fame() {
        fame++
        updateSingleStat(StatType.FAME, false)
    }

    fun defame() {
        fame--
        updateSingleStat(StatType.FAME, false)
    }

    fun setTrueMaxStats() {
        trueMaxHealth = maxHealth
        trueMaxMana = maxMana

        val equips = getInventory(ItemInventoryType.EQUIP)
        equips.items.forEach {
            if (it.key < 0) {
                val equip = it.value as ItemSlotEquip
                trueMaxHealth += equip.maxHP.toInt()
                trueMaxMana += equip.maxMP.toInt()
            }
        }
    }

    /**
     * Only use for reduction of hp
     *
     * @param health reduction of hp
     */
    fun modifyHealth(health: Int) {
        this.health += health
        if (this.health < 0) this.health = 0
        updateSingleStat(StatType.HP, false)
        updatePartyHP(false)
    }

    fun modifyMana(mana: Int) {
        this.mana += mana
        if (this.mana < 0) this.mana = 0
        updateSingleStat(StatType.MP, false)
    }

    fun heal(update: Boolean = true) {
        health = trueMaxHealth
        mana = trueMaxMana
        if (update) updateStats(ArrayList(listOf(StatType.HP, StatType.MP)), false)
    }

    fun modifyHPMP(health: Int, mana: Int) {
        this.health += health
        if (this.health > trueMaxHealth) {
            this.health = trueMaxHealth
        } else if (this.health < 0) this.health = 0

        this.mana += mana
        if (this.mana > trueMaxMana) {
            this.mana = trueMaxMana
        } else if (this.mana < 0) this.mana = 0

        updateStats(ArrayList(listOf(StatType.HP, StatType.MP)), false)
        updatePartyHP(false)
    }

    fun updatePartyHP(receive: Boolean) {
        val unmutableParty = party ?: return

        unmutableParty.getMembers().forEach {
            if (it.isOnline && it.channel == getChannel().channelId && it.field == fieldId && it.cid != id) {

                val chr = getCharacter(it.cid) ?: return@forEach
                if (receive) {
                    write(getUpdatePartyHealthPacket(chr))
                }

                chr.write(getUpdatePartyHealthPacket(this))
            }
        }
    }

    fun loadParty() {
        val party = parties[getOldPartyId(id)] ?: return
        val member = party.getMember(id) ?: return

        this.party = party
        member.loadParty(this, party)
    }

    fun loadGuild() {
        val gid = getGuildId(this)

        if (gid != -1) {
            val guild = load(gid) ?: return client.close(this, "Guild is null after database retrieval gid: $gid")

            guild.getMemberSecure(id).character = this
            guild.getMemberSecure(id).isOnline = true
            this.guild = guild
        }
    }

    fun loadMobKills() {
        mobKills.putAll(getMobKills(id))
    }

    fun updateMobKills(mob: FieldMobTemplate) {
        synchronized(mobKills) {
            killedMobs.add(mob.id)
            mobKills.merge(mob.id, 1, Int::plus)?.also { new ->
                mobKills[mob.id] = new
                if (mob.isBoss || new % 1000 == 0) {
                    message(NoticeWithoutPrefixMessage(mob.name + " killcount: " + new))
                }
            }
        }
    }

    /**
     * Damages the player $dmg over $delay time
     */
    fun damageOverTime(dmg: Int, delay: Long) {
        coroutines.register(CoroutineType.HURT, GlobalScope.launch(Dispatchers.Default) {
            while (isActive) {
                modifyHealth(-dmg)
                delay(delay)
            }
        })
    }

    fun validateStats() {
        setTrueMaxStats()
        if (health > trueMaxHealth) health = trueMaxHealth
        if (mana > trueMaxMana) mana = trueMaxMana
    }

    fun getItemQuantity(item: Int): Int {
        if (item < 1000000) {
            log(LogType.INVALID, "ItemId is 0 in getItemQuantity method", this, client)
            return 0
        }
        val type = ItemInventoryType.values()[item / 1000000 - 1]
        val inventory = getInventory(type)

        var quantity = 0
        inventory.items.values.stream()
                .filter { it.templateId == item }
                .forEach {
                    if (it is ItemSlotBundle) {
                        quantity += it.number.toInt()
                    } else {
                        quantity++
                    }
                }
        return quantity
    }

    fun hasInvSpace(item: ItemSlot): Boolean {
        return hasInvSpace(item.templateId)
    }

    fun hasInvSpace(item: Int): Boolean {
        return hasInvSpace(ItemInventoryType.values()[item / 1000000 - 1])
    }

    fun hasInvSpace(type: ItemInventoryType): Boolean {
        return getAvailableSlots(type) > 0
    }

    fun getAvailableSlots(type: ItemInventoryType): Int {
        val inv = getInventory(type)
        val used = inv.items.keys.stream()
                .filter { it > 0 }
                .filter { it <= inv.slotMax }
                .count().toShort()
        return inv.slotMax - used
    }

    fun getInventory(type: Int): ItemInventory {
        return getInventory(ItemInventoryType.values()[type])
    }

    fun getInventory(type: ItemInventoryType): ItemInventory {
        return inventories[type] ?: error("Inventory ${type.name} does not exist!")
    }

    val allInventories: Map<ItemInventoryType, ItemInventory>
        get() = inventories

    fun enableActions() {
        dispose(client)
        statUpdate(ArrayList(), true)
    }

    fun updateStats(statTypes: MutableList<StatType>, enableActions: Boolean = true) {
        statUpdate(statTypes, enableActions)
    }

    fun updateSingleStat(statType: StatType, enableActions: Boolean = true) {
        updateStats(mutableListOf(statType), enableActions)
    }

    fun write(msg: Packet) {
        client.write(msg)
    }

    fun removeMe() {
        field.removeObject(this)
    }

    override fun toString(): String {
        return "$name:$id"
    }
}