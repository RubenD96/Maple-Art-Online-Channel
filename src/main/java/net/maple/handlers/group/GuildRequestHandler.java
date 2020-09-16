package net.maple.handlers.group;

import client.Character;
import client.Client;
import client.messages.broadcast.types.AlertMessage;
import client.messages.broadcast.types.EventMessage;
import net.database.GuildAPI;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import net.maple.packets.GuildPackets;
import net.maple.packets.GuildPackets.GuildReq;
import net.maple.packets.GuildPackets.GuildRes;
import net.server.Server;
import util.HexTool;
import util.packet.PacketReader;
import world.guild.Guild;
import world.guild.GuildMember;

public class GuildRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        System.out.println("[GuildRequestHandler] " + HexTool.INSTANCE.toHex(reader.getData()));

        byte req = reader.readByte();
        switch (req) {
            case GuildReq.INVITE_GUILD: {
                Guild guild = chr.getGuild();
                if (guild == null) return;
                if (guild.getMembers().get(chr.getId()).getGrade() > 2) return;

                if (guild.getMaxSize() == guild.getMembers().size())
                    GuildPackets.message(chr, GuildRes.JOIN_GUILD_ALREADY_FULL);
                String name = reader.readMapleString();
                Character invited = c.getWorldChannel().getCharacter(name);

                if (invited != null) {
                    if (invited.getGuild() == null) {
                        chr.getGuildInvitesSent().add(name);
                        GuildPackets.sendInvite(guild, invited, chr);
                        c.write(CharacterPackets.message(new AlertMessage("Invited " + name + " to the guild.")));
                    } else {
                        GuildPackets.message(chr, GuildRes.JOIN_GUILD_ALREADY_JOINED);
                    }
                } else {
                    GuildPackets.message(chr, GuildRes.JOIN_GUILD_UNKNOWN_USER);
                }
                break;
            }
            case GuildReq.JOIN_GUILD: { // accepting request
                int gid = reader.readInteger();
                int cid = reader.readInteger();
                Guild guild = Server.INSTANCE.getGuilds().get(gid);
                if (guild == null) return;
                if (chr.getId() != cid) return;

                guild.addMember(chr);
                chr.setGuild(guild);

                guild.broadcast(GuildPackets.getJoinGuildPacket(guild, chr), chr); // guild tab update for members
                GuildPackets.changeGuildName(chr, guild.getName()); // visual character update (remote)
                GuildPackets.changeGuildMark(chr, guild.getMark()); // visual character update (remote)
                chr.write(GuildPackets.getLoadGuildPacket(guild)); // guild tab update for new member
                GuildAPI.INSTANCE.addMember(guild, chr, false);
                break;
            }
            case GuildReq.WITHDRAW_GUILD: // what a mess...
            case GuildReq.KICK_GUILD: {
                Guild guild = chr.getGuild();
                if (guild == null) return;

                int myGrade = guild.getMembers().get(chr.getId()).getGrade();
                if (req == GuildReq.KICK_GUILD) {
                    if (myGrade > 2) return;
                } else {
                    if (myGrade == 1) return;
                }

                int cid = reader.readInteger();
                if (req == GuildReq.KICK_GUILD) {
                    if (cid == chr.getId()) return;
                } else {
                    if (cid != chr.getId()) return;
                }
                String name = reader.readMapleString();
                GuildMember member = guild.getMembers().get(cid);
                if (member == null) return;
                if (req == GuildReq.KICK_GUILD)
                    if (member.getGrade() <= myGrade) return; // jr's can't kick jr's... or the master

                if (member.isOnline() && member.hasCharacter()) {
                    GuildPackets.expel(member.getCharacter());

                    if (member.getCharacter().getField() != null)
                        GuildPackets.changeGuildName(member.getCharacter(), "");
                    if (req == GuildReq.KICK_GUILD)
                        member.getCharacter().write(CharacterPackets.message(new EventMessage("You've been expelled from the guild.")));
                    else
                        member.getCharacter().write(CharacterPackets.message(new EventMessage("You've left the guild.")));

                    member.getCharacter().setGuild(null);
                    guild.getMembers().remove(cid);
                }

                GuildPackets.leave(guild, cid, name, req == GuildReq.KICK_GUILD ? GuildRes.KICK_GUILD_DONE : GuildRes.WITHDRAW_GUILD_DONE);
                GuildAPI.INSTANCE.expel(guild, cid);
                break;
            }
            case GuildReq.SET_NOTICE: {
                Guild guild = chr.getGuild();
                if (guild == null) return;
                if (guild.getMembers().get(chr.getId()).getGrade() > 2) return;

                String notice = reader.readMapleString();
                if (notice.length() > 100) return;

                guild.setNotice(notice);
                GuildPackets.setNotice(guild, notice);
                GuildAPI.INSTANCE.updateInfo(guild);
                break;
            }
            case GuildReq.SET_MEMBER_GRADE: {
                Guild guild = chr.getGuild();
                if (guild == null) return;
                int myGrade = guild.getMembers().get(chr.getId()).getGrade();
                if (myGrade > 2) return;

                int cid = reader.readInteger();
                if (cid == chr.getId()) return;
                GuildMember member = guild.getMembers().get(cid);
                if (member == null) return;
                if (member.getGrade() <= myGrade) return; // jr's can't change grade of jr's... or the master
                byte grade = reader.readByte();
                if (grade <= myGrade) return;

                GuildPackets.setMemberGrade(guild, cid, grade);
                GuildAPI.INSTANCE.updateMemberGrade(cid, grade);
                break;
            }
            case GuildReq.SET_GRADE_NAME:
                Guild guild = chr.getGuild();
                if (guild == null) return;
                int myGrade = guild.getMembers().get(chr.getId()).getGrade();
                if (myGrade != 1) return;

                for (int i = 0; i < 5; i++) {
                    String name = reader.readMapleString();
                    if (name.length() < 4 || name.length() > 10) return;
                    guild.getRanks()[i] = name;
                }

                GuildPackets.setGradeNames(guild);
                GuildAPI.INSTANCE.updateInfo(guild);
                break;
        }
    }
}
