package net.maple;

import net.maple.handlers.DoNothingHandler;
import net.maple.handlers.PacketHandler;
import net.maple.handlers.PongHandler;
import net.maple.handlers.login.MigrateInHandler;
import net.maple.handlers.misc.*;
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
        handlers.put(RecvOpcode.USER_TRANSFER_FIELD_REQUEST, new UserTransferFieldRequestHandler());
        handlers.put(RecvOpcode.USER_MOVE, new UserMoveHandler());
        handlers.put(RecvOpcode.USER_SIT_REQUEST, new UserSitRequestHandler());
        handlers.put(RecvOpcode.USER_PORTABLE_CHAIR_SIT_REQUEST, new UserPortableChairSitRequestHandler());
        handlers.put(RecvOpcode.USER_CHAT, new UserChatHandler());
        handlers.put(RecvOpcode.USER_EMOTION, new UserEmotionHandler());
        handlers.put(RecvOpcode.USER_SELECT_NPC, new UserSelectNpcHandler());
        handlers.put(RecvOpcode.USER_CHANGE_SLOT_POSITION_REQUEST, new UserChangeSlotPositionRequestHandler());
        handlers.put(RecvOpcode.USER_CHANGE_STAT_REQUEST, new UserChangeStatRequestHandler());
        handlers.put(RecvOpcode.USER_DROP_MESO_REQUEST, new UserDropMesoRequestHandler());
        handlers.put(RecvOpcode.USER_CHARACTER_INFO_REQUEST, new UserCharacterInfoRequestHandler());
        handlers.put(RecvOpcode.FUNC_KEY_MAPPED_MODIFIED, new FuncKeyMappedModifiedHandler());
        handlers.put(RecvOpcode.QUICKSLOT_KEY_MAPPED_MODIFIED, new QuickSlotKeyMappedModifiedHandler());
        handlers.put(RecvOpcode.MOB_MOVE, new MobMoveHandler());
        handlers.put(RecvOpcode.NPC_MOVE, new NPCMoveHandler());
        handlers.put(RecvOpcode.DROP_PICK_UP_REQUEST, new DropPickUpRequestHandler());

        handlers.put(RecvOpcode.UPDATE_GM_BOARD, new DoNothingHandler());
        handlers.put(RecvOpcode.UPDATE_SCREEN_SETTING, new DoNothingHandler());
        handlers.put(RecvOpcode.REQUIRE_FIELD_OBSTACLE_STATUS, new DoNothingHandler());
        handlers.put(RecvOpcode.CANCEL_INVITE_PARTY_MATCH, new DoNothingHandler());
        handlers.put(RecvOpcode.USER_QUEST_REQUEST, new DoNothingHandler()); // todo
    }
}