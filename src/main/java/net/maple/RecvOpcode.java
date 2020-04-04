package net.maple;

public enum RecvOpcode {

    MIGRATE_IN(0x14),
    PONG(0x19),
    USER_TRANSFER_FIELD_REQUEST(0x29),
    USER_MOVE(0x2C),
    USER_SIT_REQUEST(0x2D),
    USER_PORTABLE_CHAIR_SIT_REQUEST(0x2E),
    USER_CHAT(0x36),
    USER_EMOTION(0x38),
    USER_DROP_MESO_REQUEST(0x6A), // USER_DROP_MONEY_REQUEST
    USER_QUEST_REQUEST(0x77),
    FUNC_KEY_MAPPED_MODIFIED(0x9F),
    UPDATE_GM_BOARD(0xC0),
    QUICKSLOT_KEY_MAPPED_MODIFIED(0xD8),
    UPDATE_SCREEN_SETTING(0xDA),
    NPC_MOVE(0xF1),
    REQUIRE_FIELD_OBSTACLE_STATUS(0xFB),
    CANCEL_INVITE_PARTY_MATCH(0x10B);
    private int code;

    RecvOpcode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}