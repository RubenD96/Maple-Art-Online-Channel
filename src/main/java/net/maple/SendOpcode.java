package net.maple;

import util.packet.IntegerValue;

public enum SendOpcode implements IntegerValue {

    PING(0x11),
    SET_FIELD(0x8D),
    QUICKSLOT_MAPPED_INIT(0xAF),
    USER_ENTER_FIELD(0xB3),
    USER_LEAVE_FIELD(0xB4),
    USER_MOVE(0xD2),
    USER_EMOTION(0xDB),
    FUNC_KEY_MAPPED_INIT(0x18E);
    private int value;

    SendOpcode(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    public static String getEnumByString(int code) {
        for (SendOpcode op : SendOpcode.values()) {
            if (op.value == code)
                return op.name();
        }
        return null;
    }
}