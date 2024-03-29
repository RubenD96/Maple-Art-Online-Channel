package net.maple

enum class RecvOpcode(val value: Int) {

    MIGRATE_IN(0x14),
    PONG(0x19),
    CLIENT_DUMP_LOG(0x24),
    USER_TRANSFER_FIELD_REQUEST(0x29),
    USER_TRANSFER_CHANNEL_REQUEST(0x2A),
    USER_MIGRATE_TO_CASH_SHOP_REQUEST(0x2B),
    USER_MOVE(0x2C),
    USER_SIT_REQUEST(0x2D),
    USER_PORTABLE_CHAIR_SIT_REQUEST(0x2E),
    USER_MELEE_ATTACK(0x2F),
    USER_SHOOT_ATTACK(0x30),
    USER_MAGIC_ATTACK(0x31),
    USER_BODY_ATTACK(0x32),
    USER_HIT(0x34),
    USER_CHAT(0x36),
    USER_EMOTION(0x38),
    USER_SELECT_NPC(0x3F),
    USER_SCRIPT_MESSAGE_ANSWER(0x41),
    USER_SHOP_REQUEST(0x42),
    USER_STORAGE_REQUEST(0x43),
    USER_CHANGE_SLOT_POSITION_REQUEST(0x4D),
    USER_STAT_CHANGE_ITEM_USE_REQUEST(0x4E),
    USER_PET_FOOD_ITEM_USE_REQUEST(0x52),
    USER_UPGRADE_ITEM_USE_REQUEST(0x5D),
    USER_ABILITY_UP_REQUEST(0x62),
    USER_ABILITY_MASS_UP_REQUEST(0x63),
    USER_CHANGE_STAT_REQUEST(0x64),
    USER_SKILL_UP_REQUEST(0x66),
    USER_SKILL_USE_REQUEST(0x67),
    USER_SKILL_CANCEL_REQUEST(0x68),
    USER_DROP_MESO_REQUEST(0x6A), // USER_DROP_MONEY_REQUEST
    USER_GIVE_POPULARITY_REQUEST(0x6B),
    USER_CHARACTER_INFO_REQUEST(0x6D),
    USER_ACTIVATE_PET_REQUEST(0x6E),
    USER_PORTAL_SCRIPT_REQUEST(0x70),
    USER_PORTAL_TELEPORT_REQUEST(0x71),
    USER_QUEST_REQUEST(0x77),
    USER_MACRO_SYS_DATA_MODIFIED(0x7A),
    GROUP_MESSAGE(0x8C),
    WHISPER(0x8D),
    MINI_ROOM(0x90),
    PARTY_REQUEST(0x91),
    PARTY_RESULT(0x92),
    GUILD_REQUEST(0x95),
    GUILD_RESULT(0x96),
    ADMIN(0x97),
    LOG(0x98),
    FRIEND_REQUEST(0x99),
    FUNC_KEY_MAPPED_MODIFIED(0x9F),
    ALLIANCE_REQUEST(0xA7),
    ALLIANCE_RESULT(0xA8),
    GUILD_BBS(0xB3),
    USER_MIGRATE_TO_ITC_REQUEST(0xB4),
    UPDATE_GM_BOARD(0xC0),
    USER_DRAGON_BALL_SUMMON_REQUEST(0xC5),
    PET_MOVE(0xC7),
    PET_ACTION(0xC8),
    PET_INTERACTION_REQUEST(0xC9),
    QUICKSLOT_KEY_MAPPED_MODIFIED(0xD8),
    PASSIVE_SKILL_INFO_UPDATE(0xD9),
    UPDATE_SCREEN_SETTING(0xDA),
    MOB_MOVE(0xE3),
    MOB_APPLY_CTRL(0xE4),
    NPC_MOVE(0xF1),
    DROP_PICK_UP_REQUEST(0xF6),
    REACTOR_HIT(0xF9),
    REACTOR_TOUCH(0xFA),
    REQUIRE_FIELD_OBSTACLE_STATUS(0xFB),
    CANCEL_INVITE_PARTY_MATCH(0x10B),
    CASH_SHOP_QUERY_CASH_REQUEST(0x112),
    CASH_SHOP_CASH_ITEM_REQUEST(0x113);
}