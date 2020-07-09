package net.maple;

import util.packet.IntegerValue;

public enum SendOpcode implements IntegerValue {

    MIGRATE_COMMAND(0x10),
    PING(0x11),
    INVENTORY_OPERATION(0x1C),
    STAT_CHANGED(0x1E),
    GIVE_POPULARITY_RESULT(0x25),
    MESSAGE(0x26),
    CHARACTER_INFO(0x3D),
    FRIEND_RESULT(0x41),
    SET_FIELD(0x8D),
    TRANSFER_CHANNEL_REQ_IGNORED(0x94),
    QUICKSLOT_MAPPED_INIT(0xAF),
    USER_ENTER_FIELD(0xB3),
    USER_LEAVE_FIELD(0xB4),
    USER_CHAT(0xB5),
    USER_MOVE(0xD2),
    USER_MELEE_ATTACK(0xD3),
    USER_SHOOT_ATTACK(0xD4),
    USER_MAGIC_ATTACK(0xD5),
    USER_BODY_ATTACK(0xD6),
    USER_HIT(0xDA),
    USER_EMOTION(0xDB),
    USER_SET_ACTIVE_PORTABLE_CHAIR(0xDE),
    USER_AVATAR_MODIFIED(0xDF),
    USER_SIT_RESULT(0xE7),
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
    SCRIPT_MESSAGE(0x16B),
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