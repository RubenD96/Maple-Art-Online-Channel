package net.server

import cashshop.misc.CsStock
import cashshop.misc.NotSale
import client.Character
import client.command.CommandHandler
import client.party.Party
import constants.ServerConstants
import constants.ServerConstants.RANKING_TIMER
import kotlinx.coroutines.*
import managers.*
import net.database.BeautyAPI
import net.database.CharacterAPI
import net.database.GuildAPI
import net.database.ShopAPI
import scripting.dialog.npc.NPCScriptManager
import scripting.dialog.npc.Npc
import scripting.dialog.quest.Quest
import scripting.dialog.quest.QuestScriptManager
import scripting.field.Field
import scripting.field.FieldScriptManager
import scripting.portal.Portal
import scripting.portal.PortalScriptManager
import util.crypto.MapleAESOFB
import util.logging.Logger
import world.guild.Guild
import world.ranking.RankingKeeper
import java.io.File

fun main() {
    Server
}

object Server {

    val channels: MutableList<ChannelServer> = ArrayList()
    val clients: MutableMap<Int, MigrateInfo> = HashMap()
    val characters: MutableMap<Int, Character> = HashMap()
    val parties: MutableMap<Int, Party> = HashMap()
    val guilds: MutableMap<Int, Guild> = HashMap()
    var shops: List<Int>

    //cs stuff
    val notSales: MutableSet<NotSale> = HashSet()
    val csStock: MutableMap<Int, CsStock> = HashMap()

    init {
        MapleAESOFB.initialize(ServerConstants.VERSION)

        CharacterAPI.resetParties()
        shops = ShopAPI.shops

        for (i in 0 until ServerConstants.CHANNELS) {
            val channel = ChannelServer(i, 7575 + i, /*ServerConstants.IP*/ "63.251.217.1")
            channel.start()
            channels.add(channel)
        }

        GlobalScope.launch {
            withContext(NonCancellable) {
                async { rankingRoutine() }
                async { Logger.dumpBulk() }
            }
        }
        BeautyAPI.loadHairs()
        CommandHandler.loadCommands()
        loadAllScripts()
        GuildAPI.loadAllGuildNames()
        //benchmark()
    }

    private fun loadAllScripts() {
        NPCScriptManager.loadScripts<Npc>()
        QuestScriptManager.loadScripts<Quest>()
        PortalScriptManager.loadScripts<Portal>()
        FieldScriptManager.loadScripts<Field>()
    }

    private suspend fun rankingRoutine() {
        RankingKeeper.updateAllRankings()
        delay(RANKING_TIMER)
        rankingRoutine()
    }

    fun getCharacter(name: String): Character? {
        synchronized(characters) {
            return characters.values.stream()
                .filter { it.name.equals(name, ignoreCase = true) }
                .findFirst()
                .orElse(null)
        }
    }

    fun getCharacter(id: Int): Character? {
        synchronized(characters) {
            return characters[id]
        }
    }

    fun addCharacter(chr: Character) {
        synchronized(characters) {
            characters[chr.id] = chr
        }
    }

    fun removeCharacter(chr: Character) {
        synchronized(characters) {
            characters.remove(chr.id)
        }
    }

    private fun benchmark() {
        var ids = getIds("Map")
        var timeToTake: Long = System.currentTimeMillis()
        val fm = FieldManager()
        for (id in ids) {
            fm.getField(id)
        }
        println(ids.size.toString() + " fields loaded in " + (System.currentTimeMillis() - timeToTake) / 1000.0 + " seconds")

        ids = getIds("Mob")
        timeToTake = System.currentTimeMillis()
        for (id in ids) {
            MobManager.getMob(id)
        }
        println(ids.size.toString() + " mobs loaded in " + (System.currentTimeMillis() - timeToTake) / 1000.0 + " seconds")

        ids = getIds("Npc")
        timeToTake = System.currentTimeMillis()
        for (id in ids) {
            NPCManager.getNPC(id)
        }
        println(ids.size.toString() + " npcs loaded in " + (System.currentTimeMillis() - timeToTake) / 1000.0 + " seconds")

        ids = getIds("Equip")
        timeToTake = System.currentTimeMillis()
        for (id in ids) {
            ItemManager.getItem(id)
        }
        println(ids.size.toString() + " equips loaded in " + (System.currentTimeMillis() - timeToTake) / 1000.0 + " seconds")

        ids = getIds("Item")
        timeToTake = System.currentTimeMillis()
        for (id in ids) {
            ItemManager.getItem(id)
        }
        println(ids.size.toString() + " items loaded in " + (System.currentTimeMillis() - timeToTake) / 1000.0 + " seconds")

        ids = getIds("Commodity")
        timeToTake = System.currentTimeMillis()
        for (id in ids) {
            CommodityManager.getCommodity(id)
        }
        println(ids.size.toString() + " commodities loaded in " + (System.currentTimeMillis() - timeToTake) / 1000.0 + " seconds")

        ids = getIds("Quest")
        timeToTake = System.currentTimeMillis()
        for (id in ids) {
            QuestTemplateManager.getQuest(id)
        }
        println(ids.size.toString() + " quests loaded in " + (System.currentTimeMillis() - timeToTake) / 1000.0 + " seconds")
    }

    private fun getIds(loc: String): ArrayList<Int> {
        val folder = File("wz/$loc")
        val listOfFiles = folder.listFiles() ?: return ArrayList()
        val ids = ArrayList<Int>()
        for (field in listOfFiles) {
            if (field.isFile) {
                ids.add(field.name.substring(0, field.name.length - 4).toInt())
            }
        }
        return ids
    }
}