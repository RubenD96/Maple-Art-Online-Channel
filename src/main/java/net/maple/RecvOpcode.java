package net.maple;

public enum RecvOpcode {

    MIGRATE_IN(0x14),
    PONG(0x19),
    USER_MOVE(0x2C),
    USER_CHAT(0x36),
    USER_EMOTION(0x38),
    UPDATE_GM_BOARD(0xC0),
    QUICKSLOT_KEY_MAPPED_MODIFIED(0xD8),
    UPDATE_SCREEN_SETTING(0xDA),
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