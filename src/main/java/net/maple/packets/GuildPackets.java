package net.maple.packets;

import client.Character;
import net.maple.SendOpcode;
import util.packet.PacketWriter;

public class GuildPackets {

    public static void changeGuildName(Character chr, String name) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.USER_GUILD_NAME_CHANGED);
        pw.writeInt(chr.getId());
        pw.writeMapleString(name);

        chr.getField().broadcast(pw.createPacket(), chr);
    }

    public static void changeGuildMark(Character chr) {
        PacketWriter pw = new PacketWriter(8);

        pw.writeHeader(SendOpcode.USER_GUILD_MARK_CHANGED);
        pw.writeInt(chr.getId());
        pw.writeShort(0); // bg
        pw.write(0); // bg color
        pw.writeShort(0); // mark
        pw.write(0); // mark color

        chr.getField().broadcast(pw.createPacket(), chr);
    }

    public static void loadGuild(Character chr) {
        if (chr.getGuild() == null) return;

        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.GUILD_RESULT);
        pw.writeByte(GuildRes.LOAD_GUILD_DONE);
        pw.writeBool(true);
        chr.getGuild().encode(pw);

        chr.write(pw.createPacket());
    }

    public static void expel(Character chr) {
        PacketWriter pw = new PacketWriter(4);

        pw.writeHeader(SendOpcode.GUILD_RESULT);
        pw.writeByte(GuildRes.LOAD_GUILD_DONE);
        pw.writeBool(false);

        chr.write(pw.createPacket());
    }

    public static final class GuildReq {
        public static final byte LOAD_GUILD = 0x0;
        public static final byte INPUT_GUILD_NAME = 0x1;
        public static final byte CHECK_GUILD_NAME = 0x2;
        public static final byte CREATE_GUILD_AGREE = 0x3;
        public static final byte CREATE_NEW_GUILD = 0x4;
        public static final byte INVITE_GUILD = 0x5;
        public static final byte JOIN_GUILD = 0x6;
        public static final byte WITHDRAW_GUILD = 0x7;
        public static final byte KICK_GUILD = 0x8;
        public static final byte REMOVE_GUILD = 0x9;
        public static final byte INC_MAX_MEMBER_NUM = 0xA;
        public static final byte CHANGE_LEVEL = 0xB;
        public static final byte CHANGE_JOB = 0xC;
        public static final byte SET_GRADE_NAME = 0xD;
        public static final byte SET_MEMBER_GRADE = 0xE;
        public static final byte SET_MARK = 0xF;
        public static final byte SET_NOTICE = 0x10;
        public static final byte INPUT_MARK = 0x11;
        public static final byte CHECK_QUEST_WAITING = 0x12;
        public static final byte CHECK_QUEST_WAITING_2 = 0x13;
        public static final byte INSERT_QUEST_WAITING = 0x14;
        public static final byte CANCEL_QUEST_WAITING = 0x15;
        public static final byte REMOVE_QUEST_COMPLETE_GUILD = 0x16;
        public static final byte INC_POINT = 0x17;
        public static final byte INC_COMMITMENT = 0x18;
        public static final byte SET_QUEST_TIME = 0x19;
        public static final byte SHOW_GUILD_RANKING = 0x1A;
        public static final byte SET_SKILL = 0x1B;
    }

    public static final class GuildRes {
        public static final byte LOAD_GUILD_DONE = 0x1C;
        public static final byte CHECK_GUILD_NAME_AVAILABLE = 0x1D;
        public static final byte CHECK_GUILD_NAME_ALREADY_USED = 0x1E;
        public static final byte CHECK_GUILD_NAME_UNKNOWN = 0x1F;
        public static final byte CREATE_GUILD_AGREE_REPLY = 0x20;
        public static final byte CREATE_GUILD_AGREE_UNKNOWN = 0x21;
        public static final byte CREATE_NEW_GUILD_DONE = 0x22;
        public static final byte CREATE_NEW_GUILD_ALREAY_JOINED = 0x23;
        public static final byte CREATE_NEW_GUILD_GUILD_NAME_ALREAY_EXIST = 0x24;
        public static final byte CREATE_NEW_GUILD_BEGINNER = 0x25;
        public static final byte CREATE_NEW_GUILD_DISAGREE = 0x26;
        public static final byte CREATE_NEW_GUILD_NOT_FULL_PARTY = 0x27;
        public static final byte CREATE_NEW_GUILD_UNKNOWN = 0x28;
        public static final byte JOIN_GUILD_DONE = 0x29;
        public static final byte JOIN_GUILD_ALREADY_JOINED = 0x2A;
        public static final byte JOIN_GUILD_ALREADY_FULL = 0x2B;
        public static final byte JOIN_GUILD_UNKNOWN_USER = 0x2C;
        public static final byte JOIN_GUILD_UNKNOWN = 0x2D;
        public static final byte WITHDRAW_GUILD_DONE = 0x2E;
        public static final byte WITHDRAW_GUILD_NOT_JOINED = 0x2F;
        public static final byte WITHDRAW_GUILD_UNKNOWN = 0x30;
        public static final byte KICK_GUILD_DONE = 0x31;
        public static final byte KICK_GUILD_NOT_JOINED = 0x32;
        public static final byte KICK_GUILD_UNKNOWN = 0x33;
        public static final byte REMOVE_GUILD_DONE = 0x34;
        public static final byte REMOVE_GUILD_NOT_EXIST = 0x35;
        public static final byte REMOVE_GUILD_UNKNOWN = 0x36;
        public static final byte INVITE_GUILD_BLOCKED_USER = 0x37;
        public static final byte INVITE_GUILD_ALREADY_INVITED = 0x38;
        public static final byte INVITE_GUILD_REJECTED = 0x39;
        public static final byte ADMIN_CANNOT_CREATE = 0x3A;
        public static final byte ADMIN_CANNOT_INVITE = 0x3B;
        public static final byte INC_MAX_MEMBER_NUM_DONE = 0x3C;
        public static final byte INC_MAX_MEMBER_NUM_UNKNOWN = 0x3D;
        public static final byte CHANGE_LEVEL_OR_JOB = 0x3E;
        public static final byte NOTIFY_LOGIN_OR_LOGOUT = 0x3F;
        public static final byte SET_GRADE_NAME_DONE = 0x40;
        public static final byte SET_GRADE_NAME_UNKNOWN = 0x41;
        public static final byte SET_MEMBER_GRADE_DONE = 0x42;
        public static final byte SET_MEMBER_GRADE_UNKNOWN = 0x43;
        public static final byte SET_MEMBER_COMMITMENT_DONE = 0x44;
        public static final byte SET_MARK_DONE = 0x45;
        public static final byte SET_MARK_UNKNOWN = 0x46;
        public static final byte SET_NOTICE_DONE = 0x47;
        public static final byte INSERT_QUEST = 0x48;
        public static final byte NOTICE_QUEST_WAITING_ORDER = 0x49;
        public static final byte SET_GUILD_CAN_ENTER_QUEST = 0x4A;
        public static final byte INC_POINT_DONE = 0x4B;
        public static final byte SHOW_GUILD_RANKING = 0x4C;
        public static final byte GUILD_QUEST_NOT_ENOUGH_USER = 0x4D;
        public static final byte GUILD_QUEST_REGISTER_DISCONNECTED = 0x4E;
        public static final byte GUILD_QUEST_NOTICE_ORDER = 0x4F;
        public static final byte AUTHKEY_UPDATE = 0x50;
        public static final byte SET_SKILL_DONE = 0x51;
        public static final byte SERVER_MSG = 0x52;
    }
}
