package net.server

import client.Character
import client.party.Party
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import constants.ServerConstants
import managers.*
import net.database.BeautyAPI
import net.database.CharacterAPI
import net.database.DatabaseCore
import net.database.ShopAPI
import timers.RepeatDelayTimer
import util.crypto.MapleAESOFB
import world.guild.Guild
import world.ranking.RankingKeeper
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class Server private constructor() {

    val channels: MutableList<ChannelServer> = ArrayList()
    val clients: HashMap<Int, MigrateInfo> = HashMap()
    val parties: HashMap<Int, Party> = HashMap()
    val guilds: HashMap<Int, Guild> = HashMap()
    var shops: List<Int>

    init {
        MapleAESOFB.initialize(ServerConstants.VERSION)
        DatabaseCore()

        CharacterAPI.resetParties()
        shops = ShopAPI.getShops()
        for (i in 0 until ServerConstants.CHANNELS) {
            val channel = ChannelServer(i, 7575 + i, ServerConstants.IP)
            channel.start()
            channels.add(channel)
            val loginConnector = LoginConnector(this, channel)
            loginConnector.start()
            channel.loginConnector = loginConnector
        }

        // the first script engine takes a few sec to load, all subsequent engines are hella fast
        GraalJSScriptEngine.create()
        RepeatDelayTimer(1800000) { RankingKeeper.instance.updateAllRankings() }
        BeautyAPI.loadHairs()
    }

    fun getCharacter(id: Int): Character? {
        for (channel in channels) {
            val chr = channel.getCharacter(id)
            if (chr != null) {
                return chr
            }
        }
        return null
    }

    companion object {

        val instance: Server = Server()

        @JvmStatic
        fun main(args: Array<String>) {
            instance
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
            val listOfFiles = folder.listFiles()
            val ids = ArrayList<Int>()
            for (field in listOfFiles) {
                if (field.isFile) {
                    ids.add(field.name.substring(0, field.name.length - 4).toInt())
                }
            }
            return ids
        }
    }
}