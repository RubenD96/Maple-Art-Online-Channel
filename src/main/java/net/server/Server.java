package net.server;

import client.Character;
import client.party.Party;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import constants.ServerConstants;
import lombok.Getter;
import lombok.Setter;
import net.database.CharacterAPI;
import net.database.DatabaseCore;
import net.database.ShopAPI;
import util.crypto.MapleAESOFB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private static Server instance = null;
    private final @Getter List<ChannelServer> channels = new ArrayList<>();
    private @Getter final Map<Integer, MigrateInfo> clients = new HashMap<>();
    private @Getter final Map<Integer, Party> parties = new HashMap<>();
    private @Getter @Setter List<Integer> shops;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
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
        MapleAESOFB.initialize(ServerConstants.VERSION);
        new DatabaseCore();
        getInstance().run();

        // the first script engine takes a few sec to load, all subsequent engines are hella fast
        GraalJSScriptEngine.create();
    }
}
