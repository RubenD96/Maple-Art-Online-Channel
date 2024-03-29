package net.maple

import util.packet.IntegerValue

enum class SendOpcode(override val value: Int) : IntegerValue {

    MIGRATE_COMMAND(0x10),
    PING(0x11),
    INVENTORY_OPERATION(0x1C),
    STAT_CHANGED(0x1E),
    TEMPORARY_STAT_SET(0x1F),
    TEMPORARY_STAT_RESET(0x20),
    CHANGE_SKILL_RECORD_RESULT(0x23),
    GIVE_POPULARITY_RESULT(0x25),
    MESSAGE(0x26),
    GUILD_BBS(0x3B),
    CHARACTER_INFO(0x3D),
    PARTY_RESULT(0x3E),
    FRIEND_RESULT(0x41),
    GUILD_REQUEST(0x42),
    GUILD_RESULT(0x43),
    ALLIANCE_RESULT(0x44),
    BROADCAST_MSG(0x47),
    DRAGON_BALL_BOX(0x88), // wtf am i doing
    MACRO_SYS_DATA_INIT(0x8C),
    SET_FIELD(0x8D),
    SET_CASH_SHOP(0x8F),
    TRANSFER_CHANNEL_REQ_IGNORED(0x94),
    GROUP_MESSAGE(0x96),
    WHISPER(0x97),
    FIELD_EFFECT(0x9A),
    QUICKSLOT_MAPPED_INIT(0xAF),
    USER_ENTER_FIELD(0xB3),
    USER_LEAVE_FIELD(0xB4),
    USER_CHAT(0xB5),
    USER_MINI_ROOM_BALLOON(0xB8),
    USER_HIT_BY_USER(0xBF),
    USER_TESLA_TRIANGLE(0xC0),
    PET_ACTIVATED(0xC6),
    PET_MOVE(0xC9),
    PET_ACTION(0xC9),
    PET_NAME_CHANGED(0xC9),
    PET_ACTION_COMMAND(0xCD),
    USER_ITEM_UPGRADE_EFFECT(0xBA),
    USER_MOVE(0xD2),
    USER_MELEE_ATTACK(0xD3),
    USER_SHOOT_ATTACK(0xD4),
    USER_MAGIC_ATTACK(0xD5),
    USER_BODY_ATTACK(0xD6),
    USER_HIT(0xDA),
    USER_EMOTION(0xDB),
    USER_SET_ACTIVE_PORTABLE_CHAIR(0xDE),
    USER_AVATAR_MODIFIED(0xDF),
    USER_EFFECT_REMOTE(0xE0),
    USER_TEMPORARY_STAT_SET(0xE1),
    USER_TEMPORARY_STAT_RESET(0xE2),
    USER_HP(0xE3),
    USER_GUILD_NAME_CHANGED(0xE4),
    USER_GUILD_MARK_CHANGED(0xE5),
    USER_SIT_RESULT(0xE7),
    USER_EFFECT_LOCAL(0xE9),
    USER_QUEST_RESULT(0xF2),
    USER_OPEN_UI(0xFB),
    MOB_ENTER_FIELD(0x11C),
    MOB_LEAVE_FIELD(0x11D),
    MOB_CHANGE_CONTROLLER(0x11E),
    MOB_MOVE(0x11F),
    MOB_CTRL_ACK(0x120),
    MOB_HP_INDICATOR(0x12A),
    NPC_ENTER_FIELD(0x137),
    NPC_LEAVE_FIELD(0x138),
    NPC_MOVE(0x13A),
    NPC_CHANGE_CONTROLLER(0x139),
    DROP_ENTER_FIELD(0x142),
    DROP_LEAVE_FIELD(0x144),
    REACTOR_CHANGE_STATE(0x14E),
    REACTOR_MOVE(0x14F),
    REACTOR_ENTER_FIELD(0x150),
    REACTOR_LEAVE_FIELD(0x151),
    SCRIPT_MESSAGE(0x16B),
    OPEN_SHOP_DLG(0x16C),
    SHOP_RESULT(0x16D),
    STORAGE_RESULT(0x170),
    MINI_ROOM(0x175),
    CASH_SHOP_QUERY_CASH_RESULT(0x17F),
    CASH_SHOP_CASH_ITEM_RESULT(0x180),
    FUNC_KEY_MAPPED_INIT(0x18E);

    companion object {
        fun getStringByCode(code: Int): String {
            for (op in values()) {
                if (op.value == code) return op.name
            }
            return "UNKNOWN_OPCODE"
        }
    }
}