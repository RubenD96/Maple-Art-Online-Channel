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
import client.player.Skill
import client.player.StatType
import client.player.WeaponType
import client.player.friend.FriendList
import client.player.key.KeyBinding
import client.player.quest.Quest
import client.replay.MoveCollection
import client.stats.TemporaryStat
import client.stats.TemporaryStatType
import constants.UserConstants
import constants.UserConstants.expTable
import database.jooq.Tables.CHARACTERS
import field.Field
import field.obj.life.FieldControlledObject
import field.obj.life.FieldMobTemplate
import kotlinx.coroutines.*
import net.database.CharacterAPI.getKeyBindings
import net.database.CharacterAPI.getMacros
import net.database.CharacterAPI.getMobKills
import net.database.CharacterAPI.getOldPartyId
import net.database.CharacterAPI.getSkills
import net.database.CharacterAPI.getWeaponSp
import net.database.CharacterAPI.saveCharacterStats
import net.database.CharacterAPI.saveMobKills
import net.database.CharacterAPI.updateKeyBindings
import net.database.CharacterAPI.updateMacros
import net.database.CharacterAPI.updateSkills
import net.database.CharacterAPI.updateWeaponSp
import net.database.GuildAPI.getGuildId
import net.database.GuildAPI.load
import net.database.ItemAPI.saveItemInventories
import net.database.QuestAPI.saveInfo
import net.database.TownsAPI.add
import net.database.WishlistAPI.save
import net.maple.packets.CharacterPackets.message
import net.maple.packets.CharacterPackets.statUpdate
import net.maple.packets.CharacterPackets.updateMacroSettings
import net.maple.packets.ConversationPackets
import net.maple.packets.PartyPackets.getUpdatePartyHealthPacket
import net.server.ChannelServer
import net.server.Server.getCharacter
import net.server.Server.parties
import org.jooq.Record
import skill.Macro
import util.logging.LogType
import util.logging.Logger.log
import util.packet.Packet
import java.util.*
import kotlin.math.min

class Character(val client: Client, override var name: String, val record: Record) : Avatar() {

    override var id: Int = record.getValue(CHARACTERS.ID)
    var gmLevel: Int = record.getValue(CHARACTERS.GM_LEVEL)
    override var level: Int = record.getValue(CHARACTERS.LEVEL)
        set(value) {
            field = value
            updateSingleStat(StatType.LEVEL)
        }
    override var face: Int = record.getValue(CHARACTERS.FACE)
        set(value) {
            field = value
            updateSingleStat(StatType.FACE)
        }
    override var hair: Int = record.getValue(CHARACTERS.HAIR)
        set(value) {
            field = value
            updateSingleStat(StatType.HAIR)
        }
    override var gender: Int = record.getValue(CHARACTERS.GENDER)
    override var skinColor: Int = record.getValue(CHARACTERS.SKIN)
        set(value) {
            field = value
            updateSingleStat(StatType.SKIN)
        }
    override var job: Job = Job.getById(record.getValue(CHARACTERS.JOB))
        set(value) {
            field = value
            updateSingleStat(StatType.JOB)
            updateMacroSettings()
            curSp = skillpoints[value.type] ?: 0
        }
    var ap: Int = record.getValue(CHARACTERS.AP)
        set(value) {
            field = value
            updateSingleStat(StatType.AP)
        }
    var curSp: Int = 0
        set(value) {
            field = value
            updateSingleStat(StatType.SP)
            skillpoints[job.type] = value
        }
    var fame: Int = record.getValue(CHARACTERS.FAME)
        set(value) {
            field = value
            updateSingleStat(StatType.FAME)
        }
    var fieldId: Int = record.getValue(CHARACTERS.MAP)
    var spawnpoint: Int = record.getValue(CHARACTERS.SPAWNPOINT)

    var strength: Int = record.getValue(CHARACTERS.STR)
        set(value) {
            field = value
            updateSingleStat(StatType.STR)
        }
    var dexterity: Int = record.getValue(CHARACTERS.DEX)
        set(value) {
            field = value
            updateSingleStat(StatType.DEX)
        }
    var intelligence: Int = record.getValue(CHARACTERS.INT)
        set(value) {
            field = value
            updateSingleStat(StatType.INT)
        }
    var luck: Int = record.getValue(CHARACTERS.LUK)
        set(value) {
            field = value
            updateSingleStat(StatType.LUK)
        }

    var health: Int = record.getValue(CHARACTERS.HP)
        set(value) {
            field = min(value, trueMaxHealth)
            updateSingleStat(StatType.HP, false)
            updatePartyHP(false)
        }
    var maxHealth: Int = record.getValue(CHARACTERS.MAX_HP)
        set(value) {
            field = value
            updateSingleStat(StatType.MAX_HP)
        }
    var mana: Int = record.getValue(CHARACTERS.MP)
        set(value) {
            field = min(value, trueMaxMana)
            updateSingleStat(StatType.MP, false)
        }
    var maxMana: Int = record.getValue(CHARACTERS.MAX_MP)
        set(value) {
            field = value
            updateSingleStat(StatType.MAX_MP)
        }

    var exp: Int = record.getValue(CHARACTERS.EXP)
        set(value) {
            field = value
            updateSingleStat(StatType.EXP)
        }
    var meso: Int = record.getValue(CHARACTERS.MESO)
        set(value) {
            field = value
            updateSingleStat(StatType.MESO)
        }
    var totalDamage: Long = record.getValue(CHARACTERS.TOTAL_DAMAGE)
    var hardcore: Boolean = record.getValue(CHARACTERS.HARDCORE) == 1.toByte()
    override val inventories: Map<ItemInventoryType, ItemInventory> = mapOf(
        ItemInventoryType.EQUIP to ItemInventory(record.getValue(CHARACTERS.EQUIP_SLOTS)),
        ItemInventoryType.CONSUME to ItemInventory(record.getValue(CHARACTERS.CONSUME_SLOTS)),
        ItemInventoryType.INSTALL to ItemInventory(record.getValue(CHARACTERS.INSTALL_SLOTS)),
        ItemInventoryType.ETC to ItemInventory(record.getValue(CHARACTERS.ETC_SLOTS)),
        ItemInventoryType.CASH to ItemInventory(record.getValue(CHARACTERS.CASH_SLOTS))
    )

    /**
     * End constructor fields
     */
    var portal: Byte = -1
    var friendList: FriendList
    var trueMaxHealth = 0
    var trueMaxMana = 0
    var philId = 0
    var isInCashShop = false
    var cursed: Int? = null // ItemConstants.DARK_TOKEN
    var prevRand: Int = 0
    var fieldKey: Byte = 0
    var migrating = true
    var chasing = false
    var safeDeath = false

    /**
     * Collections
     */
    val quickSlotKeys = IntArray(8)
    val controlledObjects: MutableList<FieldControlledObject> = ArrayList()
    val quests: MutableMap<Int, Quest> = HashMap()
    val towns: MutableSet<Int> = TreeSet()
    val registeredQuestMobs: MutableSet<Int> = HashSet()
    val guildInvitesSent: MutableSet<String> = HashSet()
    val moveCollections = HashMap<Int, MoveCollection>()
    val coroutines = CoroutineCollection()
    val mobKills: MutableMap<Int, Int> = HashMap()
    val times: MutableMap<String, Long> = HashMap() // used for storing multiple start/end times
    val temporaryStats: MutableMap<TemporaryStatType, TemporaryStat> = EnumMap(TemporaryStatType::class.java)

    /**
     * Session updates
     */
    val killedMobs: MutableSet<Int> = HashSet()

    var keyBindings: MutableMap<Int, KeyBinding> = HashMap()
    var skillpoints: MutableMap<WeaponType, Int> = EnumMap(WeaponType::class.java)
    var skills: MutableMap<Int, Skill> = HashMap()
    var macros: MutableMap<WeaponType, MutableList<Macro>> = EnumMap(WeaponType::class.java)
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
        skillpoints = getWeaponSp(id)
        skills = getSkills(id)
        macros = getMacros(id)
        friendList = FriendList(this)
    }

    private fun resetQuickSlot() {
        val slots = intArrayOf(42, 82, 71, 73, 29, 83, 79, 81)
        System.arraycopy(slots, 0, quickSlotKeys, 0, quickSlotKeys.size)
    }

    fun save() {
        val start = System.currentTimeMillis()
        saveCharacterStats(this)
        updateKeyBindings(this)
        updateWeaponSp(this)
        updateSkills(this)
        updateMacros(this)
        saveMobKills(this)
        saveItemInventories(this)
        saveInfo(this)
        save(this)
        println("Total save in ${(System.currentTimeMillis() - start)}ms")
    }

    val isGM: Boolean
        get() = gmLevel > 0

    fun gainMeso(meso: Int, effect: Boolean = false) {
        this.meso += meso
        if (effect) {
            message(IncMesoMessage(meso))
        }
    }

    fun getChannel(): ChannelServer {
        return client.worldChannel
    }

    fun changeField(field: Field, portal: Int = 0) {
        field.enter(this, portal.toByte())
    }

    fun changeField(field: Field, portal: String) {
        field.enter(this, portal)
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
    }

    fun setJob(jobId: Int) {
        this.job = Job.getById(jobId)
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
        updatePartyHP(false)
    }

    fun modifyMana(mana: Int) {
        this.mana += mana
        if (this.mana < 0) this.mana = 0
    }

    fun heal(update: Boolean = true) {
        health = trueMaxHealth
        mana = trueMaxMana
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
        if (gid != -1) loadGuildById(gid)
    }

    fun loadGuildById(gid: Int) {
        val guild = load(gid) ?: return client.close(this, "Guild is null after database retrieval gid: $gid")

        guild.getMemberSecure(id).character = this
        guild.getMemberSecure(id).isOnline = true
        this.guild = guild
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
        statUpdate(ArrayList(), true)
    }

    private fun updateStats(statTypes: MutableList<StatType>, enableActions: Boolean = true) {
        statUpdate(statTypes, enableActions)
    }

    private fun updateSingleStat(statType: StatType, enableActions: Boolean = true) {
        updateStats(mutableListOf(statType), enableActions)
    }

    fun write(msg: Packet) {
        client.write(msg)
    }

    fun removeMe() {
        field.removeObject(this)
    }

    fun isAlive(): Boolean {
        return health > 0
    }

    fun writeNpc(npc: Int, message: String) {
        write(ConversationPackets.getOkMessagePacket(npc, 0, message))
    }

    override fun toString(): String {
        return "$name:$id"
    }
}