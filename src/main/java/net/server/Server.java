package net.server;

import constants.ServerConstants;
import lombok.Getter;
import util.crypto.MapleAESOFB;

import java.util.ArrayList;
import java.util.List;

public class Server {

    private static Server instance = null;
    private @Getter List<ChannelServer> channels = new ArrayList<>();

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private void run() {
        for (int i = 0; i < ServerConstants.CHANNELS; i++) {
            ChannelServer channel = new ChannelServer(i, 7575 + i);
            channel.start();
            channels.add(channel);
        }
    }

    public static void main(String[] args) {
        MapleAESOFB.initialize(ServerConstants.VERSION);
        getInstance().run();
    }
}
