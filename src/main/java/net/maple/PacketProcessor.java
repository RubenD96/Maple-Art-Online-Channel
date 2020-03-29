package net.maple;

import net.maple.handlers.DoNothingHandler;
import net.maple.handlers.PacketHandler;
import net.maple.handlers.PongHandler;
import net.maple.handlers.login.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class PacketProcessor {

    private static PacketProcessor instance;
    private Map<RecvOpcode, PacketHandler> handlers = new HashMap<>();

    public synchronized static PacketProcessor getInstance() {
        if (instance == null) {
            instance = new PacketProcessor();
        }
        return instance;
    }

    private PacketProcessor() {
        initializeHandlers();
    }

    public PacketHandler getHandler(short packetId) {
        return handlers.get(Arrays.stream(
                RecvOpcode.values()).filter(op -> op.getValue() == packetId)
                .findFirst().orElse(null));
    }

    public void initializeHandlers() {
        handlers.put(RecvOpcode.MIGRATE_IN, new MigrateInHandler());
        handlers.put(RecvOpcode.PONG, new PongHandler());
        handlers.put(RecvOpcode.UPDATE_SCREEN_SETTING, new DoNothingHandler());
    }
}