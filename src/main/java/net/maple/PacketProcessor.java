package net.maple;

import net.maple.handlers.DoNothingHandler;
import net.maple.handlers.PacketHandler;
import net.maple.handlers.PongHandler;
import net.maple.handlers.field.*;
import net.maple.handlers.login.*;
import net.maple.handlers.user.*;

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
        handlers.put(RecvOpcode.USER_MOVE, new UserMoveHandler());
        handlers.put(RecvOpcode.USER_SIT_REQUEST, new UserSitRequestHandler());
        handlers.put(RecvOpcode.USER_PORTABLE_CHAIR_SIT_REQUEST, new UserPortableChairSitRequestHandler());
        handlers.put(RecvOpcode.USER_CHAT, new UserChatHandler());
        handlers.put(RecvOpcode.USER_EMOTION, new UserEmotionHandler());
        handlers.put(RecvOpcode.USER_DROP_MESO_REQUEST, new UserDropMesoRequestHandler());
        handlers.put(RecvOpcode.QUICKSLOT_KEY_MAPPED_MODIFIED, new QuickSlotKeyMappedModifiedHandler());

        handlers.put(RecvOpcode.UPDATE_GM_BOARD, new DoNothingHandler());
        handlers.put(RecvOpcode.UPDATE_SCREEN_SETTING, new DoNothingHandler());
        handlers.put(RecvOpcode.REQUIRE_FIELD_OBSTACLE_STATUS, new DoNothingHandler());
        handlers.put(RecvOpcode.CANCEL_INVITE_PARTY_MATCH, new DoNothingHandler());
    }
}