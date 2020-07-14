package net.maple;

import net.maple.handlers.DoNothingHandler;
import net.maple.handlers.PacketHandler;
import net.maple.handlers.PongHandler;
import net.maple.handlers.misc.*;
import net.maple.handlers.net.MigrateInHandler;
import net.maple.handlers.user.*;
import net.maple.handlers.user.attack.AttackType;
import net.maple.handlers.user.attack.UserAttackHandler;

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
        handlers.put(RecvOpcode.USER_TRANSFER_CHANNEL_REQUEST, new UserTransferChannelRequestHandler());
        handlers.put(RecvOpcode.USER_MOVE, new UserMoveHandler());
        handlers.put(RecvOpcode.USER_SIT_REQUEST, new UserSitRequestHandler());
        handlers.put(RecvOpcode.USER_PORTABLE_CHAIR_SIT_REQUEST, new UserPortableChairSitRequestHandler());
        handlers.put(RecvOpcode.USER_HIT, new UserHitHandler());
        handlers.put(RecvOpcode.USER_CHAT, new UserChatHandler());
        handlers.put(RecvOpcode.USER_EMOTION, new UserEmotionHandler());
        handlers.put(RecvOpcode.USER_SELECT_NPC, new UserSelectNpcHandler());
        handlers.put(RecvOpcode.USER_SCRIPT_MESSAGE_ANSWER, new UserScriptMessageAnswerHandler());
        handlers.put(RecvOpcode.USER_CHANGE_SLOT_POSITION_REQUEST, new UserChangeSlotPositionRequestHandler());
        handlers.put(RecvOpcode.USER_ABILITY_UP_REQUEST, new UserAbilityUpRequestHandler());
        handlers.put(RecvOpcode.USER_ABILITY_MASS_UP_REQUEST, new UserAbilityMassUpRequestHandler());
        handlers.put(RecvOpcode.USER_CHANGE_STAT_REQUEST, new UserChangeStatRequestHandler());
        handlers.put(RecvOpcode.USER_DROP_MESO_REQUEST, new UserDropMesoRequestHandler());
        handlers.put(RecvOpcode.USER_GIVE_POPULARITY_REQUEST, new UserGivePopularityRequestHandler());
        handlers.put(RecvOpcode.USER_CHARACTER_INFO_REQUEST, new UserCharacterInfoRequestHandler());
        handlers.put(RecvOpcode.USER_PORTAL_SCRIPT_REQUEST, new UserPortalScriptRequestHandler());
        handlers.put(RecvOpcode.USER_QUEST_REQUEST, new UserQuestRequestHandler());
        handlers.put(RecvOpcode.GROUP_MESSAGE, new GroupMessageHandler());
        handlers.put(RecvOpcode.PARTY_REQUEST, new PartyRequestHandler());
        handlers.put(RecvOpcode.PARTY_RESULT, new PartyResultHandler());
        handlers.put(RecvOpcode.ADMIN, new AdminVerificationHandler());
        handlers.put(RecvOpcode.FRIEND_REQUEST, new FriendRequestHandler());
        handlers.put(RecvOpcode.USER_MIGRATE_TO_ITC_REQUEST, new UserMigrateToITCRequestHandler());
        handlers.put(RecvOpcode.FUNC_KEY_MAPPED_MODIFIED, new FuncKeyMappedModifiedHandler());
        handlers.put(RecvOpcode.QUICKSLOT_KEY_MAPPED_MODIFIED, new QuickSlotKeyMappedModifiedHandler());
        handlers.put(RecvOpcode.MOB_MOVE, new MobMoveHandler());
        handlers.put(RecvOpcode.NPC_MOVE, new NPCMoveHandler());
        handlers.put(RecvOpcode.DROP_PICK_UP_REQUEST, new DropPickUpRequestHandler());

        handlers.put(RecvOpcode.USER_MELEE_ATTACK, new UserAttackHandler(AttackType.MELEE));
        handlers.put(RecvOpcode.USER_SHOOT_ATTACK, new UserAttackHandler(AttackType.SHOOT));
        handlers.put(RecvOpcode.USER_MAGIC_ATTACK, new UserAttackHandler(AttackType.MAGIC));
        handlers.put(RecvOpcode.USER_BODY_ATTACK, new UserAttackHandler(AttackType.BODY));

        PacketHandler doNothing = new DoNothingHandler();
        handlers.put(RecvOpcode.UPDATE_GM_BOARD, doNothing);
        handlers.put(RecvOpcode.UPDATE_SCREEN_SETTING, doNothing);
        handlers.put(RecvOpcode.REQUIRE_FIELD_OBSTACLE_STATUS, doNothing);
        handlers.put(RecvOpcode.CANCEL_INVITE_PARTY_MATCH, doNothing);
    }
}