package net.maple.handlers.group

import client.Client
import client.messages.broadcast.types.AlertMessage
import client.messages.broadcast.types.EventMessage
import net.database.GuildAPI.addMember
import net.database.GuildAPI.expel
import net.database.GuildAPI.updateInfo
import net.database.GuildAPI.updateMemberGrade
import net.maple.handlers.PacketHandler
import net.maple.packets.CharacterPackets.message
import net.maple.packets.ConversationPackets
import net.maple.packets.GuildPackets
import net.maple.packets.GuildPackets.GuildReq
import net.maple.packets.GuildPackets.GuildRes
import net.maple.packets.GuildPackets.getJoinGuildPacket
import net.maple.packets.GuildPackets.getLoadGuildPacket
import net.maple.packets.GuildPackets.leave
import net.maple.packets.GuildPackets.sendInvite
import net.maple.packets.GuildPackets.setGradeNames
import net.maple.packets.GuildPackets.setMemberGrade
import net.maple.packets.GuildPackets.setNotice
import net.server.Server.getCharacter
import net.server.Server.guilds
import scripting.scripts.npc.GuildEmblemCreator
import util.HexTool.toHex
import util.logging.LogType
import util.logging.Logger.log
import util.packet.PacketReader
import world.guild.GuildMark

class GuildRequestHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val chr = c.character
        println("[GuildRequestHandler] " + toHex(reader.data))

        when (val req = reader.readByte()) {
            GuildReq.INVITE_GUILD -> {
                val guild = chr.guild ?: return
                if (guild.getMemberSecure(chr.id).grade > 2) {
                    log(LogType.HACK, "$chr tried invite to guild without proper grade", this, c)
                    return
                }

                if (guild.maxSize == guild.members.size) {
                    GuildPackets.message(chr, GuildRes.JOIN_GUILD_ALREADY_FULL)
                    log(LogType.INVALID, "guild already full", this, c)
                    return
                }

                val name = reader.readMapleString()
                val invited = getCharacter(name)
                        ?: return GuildPackets.message(chr, GuildRes.JOIN_GUILD_UNKNOWN_USER)
                if (invited.getChannel() !== c.worldChannel) return GuildPackets.message(chr, GuildRes.JOIN_GUILD_UNKNOWN_USER)

                invited.guild?.let {
                    GuildPackets.message(chr, GuildRes.JOIN_GUILD_ALREADY_JOINED)
                } ?: run {
                    chr.guildInvitesSent.add(name)
                    guild.sendInvite(invited, chr)
                    chr.message(AlertMessage("Invited $name to the guild."))
                }
            }
            GuildReq.JOIN_GUILD -> {
                // accepting request
                val gid = reader.readInteger()
                val cid = reader.readInteger()
                val guild = guilds[gid] ?: return
                if (chr.id != cid) return

                guild.addMember(chr)
                chr.guild = guild

                guild.broadcast(guild.getJoinGuildPacket(chr), chr) // guild tab update for members
                GuildPackets.changeGuildName(chr, guild.name) // visual character update (remote)
                GuildPackets.changeGuildMarkRemote(chr, guild.mark) // visual character update (remote)
                chr.write(guild.getLoadGuildPacket()) // guild tab update for new member
                addMember(guild, chr, false)
            }
            GuildReq.WITHDRAW_GUILD,
            GuildReq.KICK_GUILD,
            -> {
                val guild = chr.guild ?: return
                val myGrade = guild.getMemberSecure(chr.id).grade

                if (req == GuildReq.KICK_GUILD) {
                    if (myGrade > 2) return
                } else {
                    if (myGrade == 1) return
                }

                val cid = reader.readInteger()
                if (req == GuildReq.KICK_GUILD) {
                    if (cid == chr.id) return
                } else {
                    if (cid != chr.id) return
                }

                val name = reader.readMapleString()
                val member = guild.members[cid] ?: return

                if (req == GuildReq.KICK_GUILD) if (member.grade <= myGrade) return  // jr's can't kick jr's... or the master

                if (member.isOnline && member.hasCharacter()) {
                    member.character?.let {
                        GuildPackets.changeGuildName(it, "")
                        GuildPackets.expel(it)
                    }

                    if (req == GuildReq.KICK_GUILD) {
                        member.character?.message(EventMessage("You've been expelled from the guild."))
                    } else {
                        member.character?.message(EventMessage("You've left the guild."))
                    }
                    member.character?.guild = null
                    guild.members.remove(cid)
                }
                guild.leave(cid, name, if (req == GuildReq.KICK_GUILD) GuildRes.KICK_GUILD_DONE else GuildRes.WITHDRAW_GUILD_DONE)
                expel(guild, cid)
            }
            GuildReq.SET_MARK -> {
                val guild = chr.guild ?: return
                if (guild.leader != chr.id) return

                if (chr.meso < GuildEmblemCreator.cost) {
                    chr.write(ConversationPackets.getOkMessagePacket(2010008, 0, "You do not have the required funds!"))
                    return
                }
                chr.gainMeso(-GuildEmblemCreator.cost, true)

                val mark = GuildMark(reader)
                guild.mark = mark
            }
            GuildReq.SET_NOTICE -> {
                val guild = chr.guild ?: return
                if (guild.getMemberSecure(chr.id).grade > 2) return

                val notice = reader.readMapleString()
                if (notice.length > 100) return

                guild.notice = notice
                guild.setNotice(notice)
                updateInfo(guild)
            }
            GuildReq.SET_MEMBER_GRADE -> {
                val guild = chr.guild ?: return

                val myGrade = guild.getMemberSecure(chr.id).grade
                if (myGrade > 2) return

                val cid = reader.readInteger()
                if (cid == chr.id) return

                val member = guild.members[cid] ?: return
                if (member.grade <= myGrade) return  // jr's can't change grade of jr's... or the master

                val grade = reader.readByte()
                if (grade <= myGrade) return

                guild.setMemberGrade(cid, grade)
                updateMemberGrade(cid, grade)
            }
            GuildReq.SET_GRADE_NAME -> {
                val guild = chr.guild ?: return

                val myGrade = guild.getMemberSecure(chr.id).grade
                if (myGrade != 1) return

                for (i in 0..4) {
                    val name = reader.readMapleString()
                    if (name.length < 4 || name.length > 10) return
                    guild.ranks[i] = name
                }

                guild.setGradeNames()
                updateInfo(guild)
            }
        }
    }
}