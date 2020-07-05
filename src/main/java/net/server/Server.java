package net.server;

import constants.ServerConstants;
import lombok.Getter;
import managers.ItemManager;
import net.database.DatabaseCore;
import util.crypto.MapleAESOFB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private static Server instance = null;
    private @Getter List<ChannelServer> channels = new ArrayList<>();
    private @Getter final Map<Integer, MigrateInfo> clients = new HashMap<>();

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private void run() {
        for (int i = 0; i < ServerConstants.CHANNELS; i++) {
            ChannelServer channel = new ChannelServer(i, 7575 + i, ServerConstants.IP);
            channel.start();
            channels.add(channel);
            LoginConnector loginConnector = new LoginConnector(this, channel);
            loginConnector.start();
        }
    }

    public static void main(String[] args) {
        MapleAESOFB.initialize(ServerConstants.VERSION);
        new DatabaseCore();
        getInstance().run();
    }
}
