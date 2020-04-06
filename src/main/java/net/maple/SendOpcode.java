package net.maple;

import util.packet.IntegerValue;

public enum SendOpcode implements IntegerValue {

    PING(0x11),
    INVENTORY_OPERATION(0x1C),
    STAT_CHANGED(0x1E),
    CHARACTER_INFO(0x3D),
    SET_FIELD(0x8D),
    QUICKSLOT_MAPPED_INIT(0xAF),
    USER_ENTER_FIELD(0xB3),
    USER_LEAVE_FIELD(0xB4),
    USER_CHAT(0xB5),
    USER_MOVE(0xD2),
    USER_EMOTION(0xDB),
    USER_SET_ACTIVE_PORTABLE_CHAIR(0xDE),
    USER_SIT_RESULT(0xE7),
    NPC_ENTER_FIELD(0x137),
    NPC_LEAVE_FIELD(0x138),
    NPC_MOVE(0x13A),
    NPC_CHANGE_CONTROLLER(0x139),
    DROP_ENTER_FIELD(0x142),
    DROP_LEAVE_FIELD(0x144),
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