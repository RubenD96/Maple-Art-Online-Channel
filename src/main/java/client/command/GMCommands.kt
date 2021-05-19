package client.command

import client.Character
import client.effects.field.TrembleFieldEffect
import client.inventory.ModifyInventoriesContext
import client.inventory.item.slots.ItemSlotBundle
import client.messages.broadcast.types.AlertMessage
import client.messages.broadcast.types.NoticeMessage
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import field.obj.drop.ItemDrop
import field.obj.life.FieldMob
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import managers.ItemManager
import managers.MobManager
import managers.NPCManager
import managers.NPCShopManager
import net.database.GuildAPI
import net.database.ShopAPI
import net.maple.SendOpcode
import net.maple.handlers.user.UserSelectNpcHandler
import net.maple.handlers.user.UserUpgradeItemUseRequestHandler
import net.maple.packets.AlliancePackets
import net.maple.packets.CharacterPackets.message
import net.maple.packets.CharacterPackets.modifyInventory
import net.maple.packets.FieldPackets.fieldEffect
import net.server.Server
import net.server.Server.getCharacter
import scripting.dialog.DialogUtils
import util.packet.PacketWriter
import world.alliance.Alliance
import java.util.*
import javax.script.ScriptEngine
import javax.script.ScriptException
import kotlin.collections.ArrayList

@Suppress("unused")
class GMCommands {

    object Drop : Command {

        private var id: Int = 0
        private var quantity: Int = 0

        override val description: String = "!drop [id:int] <quantity:int>"

        override fun loadParams(params: Map<Int, String>) {
            println(params)
            id = params[0]!!.toInt()
            quantity = params[1]?.toInt() ?: 1
        }

        override fun execute(chr: Character) {
            val template = ItemManager.getItem(id)

            if (template.id == ItemManager.fallback && id != ItemManager.fallback) {
                chr.message(NoticeWithoutPrefixMessage("Item $id does not exist"))
                return
            }

            val it = template.toItemSlot()
            if (it is ItemSlotBundle) {
                it.number = quantity.toShort()
                it.title = chr.name
            }
            val drop = ItemDrop(chr.id, chr, it, 0)
            drop.field = chr.field
            drop.position = chr.position
            it.expire = Long.MAX_VALUE // never expire

            chr.field.enter(drop)
        }
    }

    object DropFiesta : Command {

        private var count: Int = 0

        override val description: String = "!dropfiesta <times:int>"

        override fun loadParams(params: Map<Int, String>) {
            count = params[0]?.toInt() ?: 1
        }

        override fun execute(chr: Character) {
            if (count <= 100) {
                repeat(count) {
                    chr.field.getObjects<FieldMob>().forEach {
                        it.drop(chr)
                    }
                }
            } else {
                chr.message(AlertMessage("Dude..."))
            }
        }
    }

    object Eval : Command {

        private var code: String = ""

        override val description: String = "!eval <code:string>"

        override fun loadParams(params: Map<Int, String>) {
            code = params.values.joinToString(" ")
        }

        override fun execute(chr: Character) {
            val engine: ScriptEngine = GraalJSScriptEngine.create()
            println("Evaluating:\n$code")
            try {
                engine.put("c", chr.client)
                engine.put("chr", chr)
                engine.put("field", chr.field)
                engine.put("server", Server)
                engine.eval(code)
            } catch (se: ScriptException) {
                se.printStackTrace()
            }
        }
    }

    object Heal : Command {

        private var target: String? = null

        override val description: String = "!heal <player:string>"

        override fun loadParams(params: Map<Int, String>) {
            target = params[0]
        }

        override fun execute(chr: Character) {
            target?.let { name ->
                getCharacter(name)?.heal() ?: run {
                    chr.message(NoticeWithoutPrefixMessage("Player $name was not found on any channel"))
                }
            } ?: run {
                chr.heal()
            }
        }
    }

    object HealMap : Command {

        override val description: String = "!healmap"

        override fun execute(chr: Character) {
            chr.field.getObjects<Character>().forEach {
                it.heal()
            }
        }
    }

    object Item : Command {

        private var id: Int = 0
        private var quantity: Int = 0

        override val description: String = "!item [id:int] <quantity:int>"

        override fun loadParams(params: Map<Int, String>) {
            id = params[0]!!.toInt()
            quantity = params[1]?.toInt() ?: 1
        }

        override fun execute(chr: Character) {
            val item = ItemManager.getItem(id)

            if (item.id == ItemManager.fallback && id != ItemManager.fallback) {
                chr.message(NoticeWithoutPrefixMessage("Item $id does not exist"))
                return
            }
            chr.modifyInventory({ i: ModifyInventoriesContext -> i.add(item, quantity.toShort()) })
        }
    }

    object DC : Command {

        private var target: String = ""

        override val description: String = "!dc/kick [name:string]"

        override fun loadParams(params: Map<Int, String>) {
            target = params[0]!!
        }

        override fun execute(chr: Character) {
            getCharacter(target)?.client?.disconnect() ?: run {
                chr.message(NoticeWithoutPrefixMessage("Player $target was not found on any channel"))
            }
        }
    }

    object Kick : Command by DC

    object Kill : Command {

        enum class Type {
            PLAYER, MOB
        }

        private var type: Type = Type.PLAYER
        private var target: String = ""

        override val description: String = "!kill [mob/player/name:string] [name:string/all]"

        override fun loadParams(params: Map<Int, String>) {
            params[0]!!.let {
                type = when (it) {
                    "player" -> {
                        target = params[1]!!
                        Type.PLAYER
                    }
                    "players" -> {
                        target = "all"
                        Type.PLAYER
                    }
                    "mobs" -> Type.MOB
                    "mob" -> Type.MOB
                    else -> {
                        if (it.length <= 3) {
                            throw Exception()
                        }
                        target = it
                        Type.PLAYER
                    }
                }
            }
        }

        override fun execute(chr: Character) {
            when (type) {
                Type.PLAYER -> {
                    if (target == "all") {
                        chr.field.getObjects<Character>().forEach {
                            if (!it.isGM) {
                                it.safeDeath = true
                                it.health = 0
                                it.mana = 0
                            }
                        }
                    } else {
                        getCharacter(target)?.let {
                            it.safeDeath = true
                            it.health = 0
                            it.mana = 0
                        } ?: run {
                            chr.message(NoticeWithoutPrefixMessage("Player $target was not found on any channel"))
                        }
                    }
                }
                Type.MOB -> {
                    chr.field.getObjects<FieldMob>().forEach {
                        it.kill(chr)
                    }
                }
            }
        }
    }

    object Level : Command {

        private var level: Int = 0

        override val description: String = "!level [newlevel:int]"

        override fun loadParams(params: Map<Int, String>) {
            level = params[0]!!.toInt()
        }

        override fun execute(chr: Character) {
            chr.level = level
        }
    }

    object LevelUp : Command {

        private var levels: Int = 0

        override val description: String = "!levelup <amount:int>"

        override fun loadParams(params: Map<Int, String>) {
            levels = params[0]?.toInt() ?: 1
        }

        override fun execute(chr: Character) {
            repeat(levels) {
                chr.levelUp()
            }
        }
    }

    object NPC : Command {

        private var id: Int = 0
        private var flipped: Boolean = false

        override val description: String = "!npc [id:int] <flip>"

        override fun loadParams(params: Map<Int, String>) {
            id = params[0]!!.toInt()
            flipped = params[1]?.equals("flip") ?: false
        }

        override fun execute(chr: Character) {
            val npc = NPCManager.getNPC(id)

            if (npc.id == NPCManager.fallback && id != NPCManager.fallback) {
                chr.message(NoticeWithoutPrefixMessage("NPC $id does not exist"))
                return
            }

            npc.rx0 = chr.position.x + 50
            npc.rx1 = chr.position.x - 50
            npc.position = chr.position
            npc.foothold = chr.foothold
            npc.f = !flipped
            npc.cy = chr.position.y
            npc.hide = false
            npc.field = chr.field

            chr.field.enter(npc)
        }
    }

    object Online : Command {

        override val description: String = "!online"

        override fun execute(chr: Character) {
            val channels = TreeMap<Int, MutableList<Character>>()
            Server.channels.forEach {
                channels[it.channelId] = ArrayList()
            }

            Server.characters.forEach {
                channels[it.value.getChannel().channelId]?.add(it.value)
            }

            channels.forEach {
                chr.message(NoticeWithoutPrefixMessage("Channel ${it.key}:"))
                it.value.forEach { player ->
                    chr.message(NoticeWithoutPrefixMessage("-> ${player.name} - ${player.field.template.name} (${player.fieldId})"))
                }
            }
        }
    }

    object OpenNPC : Command {

        private var id: Int = 0

        override val description: String = "!opennpc [id:int]"

        override fun loadParams(params: Map<Int, String>) {
            id = params[0]!!.toInt()
        }

        override fun execute(chr: Character) {
            val npc = NPCManager.getNPC(id)

            if (npc.id == NPCManager.fallback && id != NPCManager.fallback) {
                chr.message(NoticeWithoutPrefixMessage("NPC $id does not exist"))
                return
            }

            UserSelectNpcHandler.openNpc(chr.client, npc)
        }
    }

    object Pos : Command {

        private var target: String? = ""

        override val description: String = "!pos <name:string>"

        override fun loadParams(params: Map<Int, String>) {
            target = params[0]
        }

        override fun execute(chr: Character) {
            target?.let { name ->
                getCharacter(name)?.let {
                    chr.message(NoticeWithoutPrefixMessage("Player $name is at pos: ${it.position}"))
                } ?: run {
                    chr.message(NoticeWithoutPrefixMessage("Player $name was not found on any channel"))
                }
            } ?: run {
                chr.message(NoticeWithoutPrefixMessage("Your position is: ${chr.position}"))
            }
        }
    }

    object ReloadMap : Command {

        private var id: Int? = 0

        override val description: String = "!reloadmap <id:int>"

        override fun loadParams(params: Map<Int, String>) {
            id = params[0]?.toInt()
        }

        override fun execute(chr: Character) {
            Server.channels.forEach { channel ->
                val players = channel.fieldManager.getField(id ?: chr.fieldId).getObjects<Character>()
                channel.fieldManager.reloadField(id ?: chr.fieldId)

                players.forEach {
                    it.changeField(id ?: chr.fieldId)
                    it.message(NoticeMessage("The map got reloaded by a GM"))
                }
            }

            id?.let {
                chr.message(AlertMessage("Map $id reloaded"))
            }
        }
    }

    object ReloadShops : Command {

        override val description: String = "!reloadshops"

        override fun execute(chr: Character) {
            Server.shops = ShopAPI.shops
            NPCShopManager.reload()
            chr.message(AlertMessage("Updated all shops!"))
        }
    }

    object Sense : Command {

        override val description: String = "!sense"

        override fun execute(chr: Character) {
            UserSelectNpcHandler.openNpc(chr.client, NPCManager.getNPC(1002002))
        }
    }

    object SetNX : Command {

        private var target: String = ""
        private var amount: Int = 0

        override val description: String = "!setnx [name:string] [amount:int]"

        override fun loadParams(params: Map<Int, String>) {
            target = params[0]!!
            amount = params[1]!!.toInt()
        }

        override fun execute(chr: Character) {
            getCharacter(target)?.let {
                it.client.cash = amount
                it.message(AlertMessage("Your NX balance was set to $amount by a GM"))
            } ?: run {
                chr.message(NoticeWithoutPrefixMessage("Player $target was not found on any channel"))
            }
        }
    }

    object Spawn : Command {

        private var mobId: Int = 0
        private var amount: Int = 0

        override val description: String = "!mob [id:int] <amount:int>"

        override fun loadParams(params: Map<Int, String>) {
            mobId = params[0]!!.toInt()
            amount = params[1]?.toInt() ?: 1
        }

        override fun execute(chr: Character) {
            repeat(amount) {
                val template = MobManager.getMob(mobId)
                val mob = FieldMob(template, false)
                mob.hp = mob.template.maxHP
                mob.mp = mob.template.maxMP

                mob.position = chr.position
                mob.foothold = chr.foothold
                mob.f = true
                mob.cy = chr.position.y
                mob.hide = false
                mob.field = chr.field

                chr.field.enter(mob)
            }
        }
    }

    object Summon : Command by Spawn

    object Tremble : Command {

        override val description: String = "!tremble"

        override fun execute(chr: Character) {
            chr.field.fieldEffect(TrembleFieldEffect(true, 0))
        }
    }

    object Shake : Command by Tremble

    object WhereAmI : Command {

        override val description: String = "!whereami"

        override fun execute(chr: Character) {
            chr.message(NoticeWithoutPrefixMessage("You're in map ${chr.fieldId}"))
        }
    }

    object MapId : Command by WhereAmI

    object Scroll : Command {

        private var success: Boolean = false
        private var cursed: Boolean = false
        private var whiteScroll: Boolean = false
        private var v6: Byte = 0

        private var enchantSkill: Boolean = false
        private var enchantCategory: Int = 0

        override val description: String =
            "!scroll [success:boolean] <cursed:boolean> <v5:byte> <v6:byte> <enchantSkill:boolean> <enchantCategory:int>"

        override fun loadParams(params: Map<Int, String>) {
            success = params[0]!!.toInt() == 1
            cursed = params[1]?.toInt() == 1
            whiteScroll = params[2]?.toInt() == 1
            v6 = params[3]?.toByte() ?: 0

            enchantSkill = params[4]?.toInt() == 1
            enchantCategory = params[5]?.toInt() ?: 0
        }

        override fun execute(chr: Character) {
            chr.field.broadcast(
                UserUpgradeItemUseRequestHandler.getShowItemUpgradeEffectPacket(
                    chr.id,
                    success,
                    cursed,
                    enchantSkill,
                    enchantCategory,
                    whiteScroll,
                    v6
                )
            )
        }
    }

    object Job : Command {

        private var id: Int = 0

        override val description: String = "!job [id:int]"

        override fun loadParams(params: Map<Int, String>) {
            id = params[0]!!.toInt()
        }

        override fun execute(chr: Character) {
            chr.setJob(id)
        }
    }

    object RestartReplay : Command {

        override val description: String = "!restartreplay"

        override fun execute(chr: Character) {
            chr.field.startReplay()
        }
    }

    object Letters : Command {

        private var letters: String = ""

        override val description: String = "!letters [letters:string]"

        override fun loadParams(params: Map<Int, String>) {
            letters = params.values.joinToString(" ")
        }

        override fun execute(chr: Character) {
            val left = chr.moveAction % 2 == 1
            if (left) {
                letters = letters.reversed()
            }

            val pos = chr.position
            letters.forEach {
                if (left) pos.x -= 30
                else pos.x += 30

                val letter = DialogUtils.convert(it)
                if (letter != -1) {
                    val drop = ItemDrop(chr.id, chr, ItemManager.getItem(letter).toItemSlot(), 0)
                    drop.field = chr.field
                    drop.position = pos
                    drop.expire = System.currentTimeMillis() + 300000
                    chr.field.enter(drop)
                }
            }
        }
    }

    object Stat : Command {

        private var type: String = ""
        private var value: Int = 0

        override val description: String = "!stat [type:string] [value:int]"

        override fun loadParams(params: Map<Int, String>) {
            type = params[0]!!
            value = params[1]!!.toInt()
        }

        override fun execute(chr: Character) {
            when (type.toLowerCase()) {
                "level" -> chr.level = value
                "face" -> chr.face = value
                "hair" -> chr.hair = value
                "gender" -> chr.gender = value
                "skin" -> chr.skinColor = value
                "job" -> chr.job = client.player.Job.getById(value)
                "ap" -> chr.ap = value
                "sp" -> chr.sp = value
                "fame" -> chr.fame = value
                "str" -> chr.strength = value
                "dex" -> chr.dexterity = value
                "int" -> chr.intelligence = value
                "luk" -> chr.luck = value
                "hp" -> chr.health = value
                "mhp" -> chr.maxHealth = value
                "mp" -> chr.mana = value
                "mmp" -> chr.maxMana = value
                "exp" -> chr.exp = value
                "meso" -> chr.meso = value
                else -> chr.message(
                    NoticeWithoutPrefixMessage(
                        "Invalid type \"$type\", correct types: " +
                                "level | face | hair | gender | skin | job | ap | sp | fame | str | dex | int | luk | hp | mhp | mp | mmp | exp | meso)"
                    )
                )
            }

            chr.validateStats()
        }
    }

    object CreateAlliance : Command {

        override val description: String = "test"

        override fun execute(chr: Character) {
            val alliance = Alliance(1, "Test")
            alliance.guilds.add(chr.guild!!.also { it.alliance = alliance })
            chr.alliance = alliance

            Server.guilds[2]?.let {
                alliance.guilds.add(it)
            } ?: run {
                alliance.guilds.add(GuildAPI.load(2)!!)
            }

            AlliancePackets.create(alliance)
            GlobalScope.launch {
                delay(1000)
                AlliancePackets.load(chr, alliance)
                delay(1000)
                AlliancePackets.setGradeName(alliance)
            }
        }
    }

    object Yeet : Command {

        override val description: String = "Yeet yo self"

        override fun execute(chr: Character) {
            val pw = PacketWriter(3)

            pw.writeHeader(SendOpcode.USER_OPEN_UI)
            pw.write(0x1F)

            chr.write(pw.createPacket())
            chr.message(NoticeWithoutPrefixMessage("Yeeted"))
        }
    }

    object OpenUI : Command {

        private enum class UI(val value: Byte) {
            ITEM(0x00),
            EQUIP(0x01),
            STAT(0x02),
            SKILL(0x03),
            MINIMAP(0x04),
            KEYCONFIG(0x05),
            QUESTINFO(0x06),
            USERLIST(0x07),
            MESSENGER(0x08),
            MONSTERBOOK(0x09),
            USERINFO(0x0A),
            SHORTCUT(0x0B),
            MENU(0x0C),
            QUESTALARM(0x0D),
            PARTYHP(0x0E),
            QUESTTIMER(0x0F),
            QUESTTIMERACTION(0x10),
            MONSTERCARNIVAL(0x11),
            ITEMSEARCH(0x12),
            ENERGYBAR(0x13),
            GUILDBOARD(0x14),
            PARTYSEARCH(0x15),
            ITEMMAKE(0x16),
            CONSULT(0x17),
            CLASSCOMPETITION(0x18),
            RANKING(0x19),
            FAMILY(0x1A),
            FAMILYCHART(0x1B),
            OPERATORBOARD(0x1C),
            OPERATORBOARDSTATE(0x1D),
            MEDALQUESTINFO(0x1E),
            WEBEVENT(0x1F),
            SKILLEX(0x20),
            REPAIRDURABILITY(0x21),
            CHATWND(0x22),
            BATTLERECORD(0x23),
            GUILDMAKEMARK(0x24),
            GUILDMAKE(0x25),
            GUILDRANK(0x26),
            GUILDBBS(0x27),
            ACCOUNTMOREINFO(0x28),
            FINDFRIEND(0x29),
            DRAGONBOX(0x2A),
            WNDNO(0x2B),
            UNRELEASE(0x2C);

            companion object {
                fun findByName(name: String): UI? {
                    return values().find { it.name.equals(name, ignoreCase = true) }
                }
            }
        }

        private var name: String? = null

        override val description: String = "!openui [ui:string]"

        override fun loadParams(params: Map<Int, String>) {
            name = params[0]!!
        }

        override fun execute(chr: Character) {
            UI.findByName(name!!)?.let {
                val pw = PacketWriter(3)

                pw.writeHeader(SendOpcode.USER_OPEN_UI)
                pw.writeByte(it.value)

                chr.write(pw.createPacket())
                chr.message(NoticeWithoutPrefixMessage("[OpenUI] Attempted to open $name"))
            } ?: chr.message(NoticeWithoutPrefixMessage("[OpenUI] Could not find UI: $name"))
        }
    }

    object UI : Command by OpenUI
}