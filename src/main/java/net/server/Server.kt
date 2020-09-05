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
import java.io.BufferedReader
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

/*
package net.server;

import client.Character;
import client.party.Party;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import constants.ServerConstants;
import lombok.Getter;
import lombok.Setter;
import managers.*;
import net.database.BeautyAPI;
import net.database.CharacterAPI;
import net.database.DatabaseCore;
import net.database.ShopAPI;
import timers.RepeatDelayTimer;
import util.crypto.MapleAESOFB;
import world.guild.Guild;
import world.ranking.RankingKeeper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private static Server instance = null;
    private final @Getter List<ChannelServer> channels = new ArrayList<>();
    private @Getter final Map<Integer, MigrateInfo> clients = new HashMap<>();
    private @Getter final Map<Integer, Party> parties = new HashMap<>();
    private @Getter final Map<Integer, Guild> guilds = new HashMap<>();
    private @Getter @Setter List<Integer> shops;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private Server() {
        MapleAESOFB.initialize(ServerConstants.VERSION);
        new DatabaseCore();
        run();

        // the first script engine takes a few sec to load, all subsequent engines are hella fast
        GraalJSScriptEngine.create();
        new RepeatDelayTimer(1800000, () -> RankingKeeper.getInstance().updateAllRankings());
        BeautyAPI.loadHairs();
    }

    public Character getCharacter(int id) {
        for (ChannelServer channel : channels) {
            Character chr = channel.getCharacter(id);
            if (chr != null) {
                return chr;
            }
        }
        return null;
    }

    private void run() {
        CharacterAPI.resetParties();
        shops = ShopAPI.getShops();
        for (int i = 0; i < ServerConstants.CHANNELS; i++) {
            ChannelServer channel = new ChannelServer(i, 7575 + i, ServerConstants.IP);
            channel.start();
            channels.add(channel);
            LoginConnector loginConnector = new LoginConnector(this, channel);
            loginConnector.start();
            channel.setLoginConnector(loginConnector);
        }
    }

    public static void main(String[] args) {
        getInstance();
    }

    private static void benchmark() {
        long timeToTake;

        ArrayList<Integer> ids = getIds("Map");
        timeToTake = System.currentTimeMillis();
        FieldManager fm = new FieldManager();
        for (int id : ids) {
            fm.getField(id);
        }
        System.out.println(ids.size() + " fields loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        ids = getIds("Mob");
        timeToTake = System.currentTimeMillis();
        for (int id : ids) {
            MobManager.getMob(id);
        }
        System.out.println(ids.size() + " mobs loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        ids = getIds("Npc");
        timeToTake = System.currentTimeMillis();
        for (int id : ids) {
            NPCManager.getNPC(id);
        }
        System.out.println(ids.size() + " npcs loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        ids = getIds("Equip");
        timeToTake = System.currentTimeMillis();
        for (int id : ids) {
            ItemManager.getItem(id);
        }
        System.out.println(ids.size() + " equips loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        ids = getIds("Item");
        timeToTake = System.currentTimeMillis();
        for (int id : ids) {
            ItemManager.getItem(id);
        }
        System.out.println(ids.size() + " items loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        ids = getIds("Commodity");
        timeToTake = System.currentTimeMillis();
        for (int id : ids) {
            CommodityManager.getCommodity(id);
        }
        System.out.println(ids.size() + " commodities loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");

        ids = getIds("Quest");
        timeToTake = System.currentTimeMillis();
        for (int id : ids) {
            QuestTemplateManager.getQuest(id);
        }
        System.out.println(ids.size() + " quests loaded in " + ((System.currentTimeMillis() - timeToTake) / 1000.0) + " seconds");
    }

    private static ArrayList<Integer> getIds(String loc) {
        File folder = new File("wz/" + loc);
        File[] listOfFiles = folder.listFiles();

        ArrayList<Integer> ids = new ArrayList<>();
        for (File field : listOfFiles) {
            if (field.isFile()) {
                ids.add(Integer.parseInt(field.getName().substring(0, field.getName().length() - 4)));
            }
        }
        return ids;
    }
}

 */