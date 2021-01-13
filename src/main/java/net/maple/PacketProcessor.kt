package net.maple

import net.maple.handlers.DoNothingHandler
import net.maple.handlers.PacketHandler
import net.maple.handlers.group.*
import net.maple.handlers.item.UserStatChangeItemUseRequestHandler
import net.maple.handlers.misc.*
import net.maple.handlers.mob.MobApplyCtrlHandler
import net.maple.handlers.mob.MobMoveHandler
import net.maple.handlers.net.ClientDumpLogHandler
import net.maple.handlers.net.MigrateInHandler
import net.maple.handlers.net.PongHandler
import net.maple.handlers.user.*
import net.maple.handlers.user.attack.AttackType
import net.maple.handlers.user.attack.UserAttackHandler
import java.util.*

object PacketProcessor {

    private val handlers: MutableMap<RecvOpcode, PacketHandler> = EnumMap(RecvOpcode::class.java)

    fun getHandler(packetId: Short): PacketHandler? {
        return handlers[Arrays.stream(
                RecvOpcode.values()).filter { it.value == packetId.toInt() }
                .findFirst().orElse(null)]
    }

    init {
        handlers[RecvOpcode.MIGRATE_IN] = MigrateInHandler()
        handlers[RecvOpcode.PONG] = PongHandler()
        handlers[RecvOpcode.CLIENT_DUMP_LOG] = ClientDumpLogHandler()
        handlers[RecvOpcode.USER_TRANSFER_FIELD_REQUEST] = UserTransferFieldRequestHandler()
        handlers[RecvOpcode.USER_TRANSFER_CHANNEL_REQUEST] = UserTransferChannelRequestHandler()
        handlers[RecvOpcode.USER_MIGRATE_TO_CASH_SHOP_REQUEST] = UserMigrateToCashShopRequestHandler()
        handlers[RecvOpcode.USER_MOVE] = UserMoveHandler()
        handlers[RecvOpcode.USER_SIT_REQUEST] = UserSitRequestHandler()
        handlers[RecvOpcode.USER_PORTABLE_CHAIR_SIT_REQUEST] = UserPortableChairSitRequestHandler()
        handlers[RecvOpcode.USER_HIT] = UserHitHandler()
        handlers[RecvOpcode.USER_CHAT] = UserChatHandler()
        handlers[RecvOpcode.USER_EMOTION] = UserEmotionHandler()
        handlers[RecvOpcode.USER_SELECT_NPC] = UserSelectNpcHandler()
        handlers[RecvOpcode.USER_SCRIPT_MESSAGE_ANSWER] = UserScriptMessageAnswerHandler()
        handlers[RecvOpcode.USER_SHOP_REQUEST] = UserShopRequestHandler()
        handlers[RecvOpcode.USER_STORAGE_REQUEST] = UserStorageRequestHandler()
        handlers[RecvOpcode.USER_CHANGE_SLOT_POSITION_REQUEST] = UserChangeSlotPositionRequestHandler()
        handlers[RecvOpcode.USER_STAT_CHANGE_ITEM_USE_REQUEST] = UserStatChangeItemUseRequestHandler()
        handlers[RecvOpcode.USER_ABILITY_UP_REQUEST] = UserAbilityUpRequestHandler()
        handlers[RecvOpcode.USER_ABILITY_MASS_UP_REQUEST] = UserAbilityMassUpRequestHandler()
        handlers[RecvOpcode.USER_CHANGE_STAT_REQUEST] = UserChangeStatRequestHandler()
        handlers[RecvOpcode.USER_SKILL_UP_REQUEST] = UserSkillUpRequestHandler()
        handlers[RecvOpcode.USER_SKILL_USE_REQUEST] = UserSkillUseRequestHandler()
        handlers[RecvOpcode.USER_SKILL_CANCEL_REQUEST] = UserSkillCancelRequestHandler()
        handlers[RecvOpcode.USER_DROP_MESO_REQUEST] = UserDropMesoRequestHandler()
        handlers[RecvOpcode.USER_GIVE_POPULARITY_REQUEST] = UserGivePopularityRequestHandler()
        handlers[RecvOpcode.USER_CHARACTER_INFO_REQUEST] = UserCharacterInfoRequestHandler()
        handlers[RecvOpcode.USER_PORTAL_SCRIPT_REQUEST] = UserPortalScriptRequestHandler()
        handlers[RecvOpcode.USER_QUEST_REQUEST] = UserQuestRequestHandler()
        handlers[RecvOpcode.GROUP_MESSAGE] = GroupMessageHandler()
        handlers[RecvOpcode.PARTY_REQUEST] = PartyRequestHandler()
        handlers[RecvOpcode.PARTY_RESULT] = PartyResultHandler()
        handlers[RecvOpcode.GUILD_REQUEST] = GuildRequestHandler()
        handlers[RecvOpcode.GUILD_RESULT] = GuildResultHandler()
        handlers[RecvOpcode.ADMIN] = AdminVerificationHandler()
        handlers[RecvOpcode.FRIEND_REQUEST] = FriendRequestHandler()
        handlers[RecvOpcode.USER_MIGRATE_TO_ITC_REQUEST] = UserMigrateToITCRequestHandler()
        handlers[RecvOpcode.FUNC_KEY_MAPPED_MODIFIED] = FuncKeyMappedModifiedHandler()
        handlers[RecvOpcode.QUICKSLOT_KEY_MAPPED_MODIFIED] = QuickSlotKeyMappedModifiedHandler()
        handlers[RecvOpcode.MOB_MOVE] = MobMoveHandler()
        handlers[RecvOpcode.MOB_APPLY_CTRL] = MobApplyCtrlHandler()
        handlers[RecvOpcode.NPC_MOVE] = NPCMoveHandler()
        handlers[RecvOpcode.DROP_PICK_UP_REQUEST] = DropPickUpRequestHandler()
        handlers[RecvOpcode.REACTOR_HIT] = ReactorHitHandler()
        handlers[RecvOpcode.CASH_SHOP_QUERY_CASH_REQUEST] = CashShopQueryCashRequestHandler()
        handlers[RecvOpcode.CASH_SHOP_CASH_ITEM_REQUEST] = CashShopCashItemRequestHandler()
        handlers[RecvOpcode.USER_MELEE_ATTACK] = UserAttackHandler(AttackType.MELEE)
        handlers[RecvOpcode.USER_SHOOT_ATTACK] = UserAttackHandler(AttackType.SHOOT)
        handlers[RecvOpcode.USER_MAGIC_ATTACK] = UserAttackHandler(AttackType.MAGIC)
        handlers[RecvOpcode.USER_BODY_ATTACK] = UserAttackHandler(AttackType.BODY)

        val doNothing: PacketHandler = DoNothingHandler()
        handlers[RecvOpcode.UPDATE_GM_BOARD] = doNothing
        handlers[RecvOpcode.UPDATE_SCREEN_SETTING] = doNothing
        handlers[RecvOpcode.REQUIRE_FIELD_OBSTACLE_STATUS] = doNothing
        handlers[RecvOpcode.CANCEL_INVITE_PARTY_MATCH] = doNothing
        handlers[RecvOpcode.PASSIVE_SKILL_INFO_UPDATE] = doNothing
    }
}