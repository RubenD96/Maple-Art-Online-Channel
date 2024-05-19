package net.maple.handlers.group

import client.Client
import net.database.GuildAPI
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import util.logging.LogType
import util.logging.Logger
import util.packet.PacketReader
import util.packet.PacketWriter
import world.guild.Guild
import world.guild.bbs.BBSItem
import world.guild.bbs.GuildBBS

class GuildBBSHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        val guild: Guild = c.character.guild ?: return

        when (reader.readByte()) {
            REGISTER -> onRegisterEntry(c, reader, guild.bbs)
            DELETE -> onDeleteEntry(c, guild.bbs, reader.readInteger())
            LOAD_LIST_REQUEST -> loadList(c, guild.bbs)
            VIEW_ENTRY_REQUEST -> {
                val item = guild.bbs.getById(reader.readInteger()) ?: return loadList(c, guild.bbs)
                loadEntry(c, item)
            }
            REGISTER_COMMENT -> onRegisterComment(c, reader, guild.bbs)
            DELETE_COMMENT -> onDeleteComment(c, reader, guild.bbs)
        }
    }

    companion object {
        private const val REGISTER: Byte = 0x00
        private const val DELETE: Byte = 0x01
        private const val LOAD_LIST_REQUEST: Byte = 0x02
        private const val VIEW_ENTRY_REQUEST: Byte = 0x03
        private const val REGISTER_COMMENT: Byte = 0x04
        private const val DELETE_COMMENT: Byte = 0x05
        private const val LOAD_LIST_RESULT: Byte = 0x06
        private const val VIEW_ENTRY_RESULT: Byte = 0x07
        private const val ENTRY_NOT_FOUND: Byte = 0x08

        private fun onRegisterEntry(c: Client, reader: PacketReader, bbs: GuildBBS) {
            val modify = reader.readBool()
            var id = -1
            if (modify) {
                id = reader.readInteger()
            }

            val notice = reader.readBool()
            val title = reader.readMapleString()
            if (title.length > 25) return

            val content = reader.readMapleString()
            if (title.length > 600) return

            val emote = reader.readInteger()
            if (emote in 0x64..0x6A) {
                if (c.character.getItemQuantity(5290000 + emote - 0x64) == 0) {
                    Logger.log(LogType.INVALID, "Emote item not in possession", this, c)
                    c.close(this, "Emote item not in possession")
                    return
                }
            } else if (emote < 0 || emote > 3) {
                Logger.log(LogType.INVALID, "Invalid base emote", this, c)
                c.close(this, "Invalid base emote")
                return
            }

            val item: BBSItem
            if (modify) {
                item = bbs.getById(id) ?: return loadList(c, bbs)
                if (item.cid != c.character.id) return loadList(c, bbs)
                item.title = title
                item.content = content
                item.emote = emote

                GuildAPI.updateBBSItem(bbs.guildId, item)
            } else {
                item = bbs.addItem(c.character.id, title, content, emote, notice)
                GuildAPI.addBBSItem(bbs.guildId, item)
            }
            loadEntry(c, item)
        }

        private fun onDeleteEntry(c: Client, bbs: GuildBBS, id: Int) {
            val chr = c.character
            val item = bbs.getById(id) ?: return loadList(c, bbs)
            if (chr.id != item.cid && chr.guild!!.getMemberSecure(chr.id).grade > 2) {
                Logger.log(LogType.HACK, "Trying to remove a thread without proper permissions", this, c)
                c.close()
                return
            }
            bbs.removeItem(item)
            GuildAPI.deleteBBSItem(bbs.guildId, item)
        }

        private fun loadList(c: Client, bbs: GuildBBS) {
            val pw = PacketWriter(16)

            pw.writeHeader(SendOpcode.GUILD_BBS)
            pw.writeByte(LOAD_LIST_RESULT)

            bbs.getNoticeItem()?.let {
                pw.writeBool(true)
                it.encodeSimple(pw)
            } ?: pw.writeBool(false)

            val items = bbs.getRegularItems()
            pw.writeInt(items.size) // list size
            pw.writeInt(items.size) // ???
            items.forEach { it.encodeSimple(pw) }

            c.write(pw.createPacket())
        }

        private fun loadEntry(c: Client, item: BBSItem) {
            val pw = PacketWriter(16)

            pw.writeHeader(SendOpcode.GUILD_BBS)
            pw.writeByte(VIEW_ENTRY_RESULT)

            item.encodeDetailed(pw)

            c.write(pw.createPacket())
        }

        private fun onRegisterComment(c: Client, reader: PacketReader, bbs: GuildBBS) {
            val item = bbs.getById(reader.readInteger()) ?: return loadList(c, bbs)
            val content = reader.readMapleString()
            if (content.length > 25) return

            val comment = item.addComment(c.character.id, content)
            GuildAPI.addBBSComment(bbs.guildId, item.id, comment)

            loadEntry(c, item)
        }

        private fun onDeleteComment(c: Client, reader: PacketReader, bbs: GuildBBS) {
            val chr = c.character
            val item = bbs.getById(reader.readInteger()) ?: return loadList(c, bbs)
            val comment = item.getById(reader.readInteger()) ?: return loadEntry(c, item)

            if (chr.id != comment.cid && chr.guild!!.getMemberSecure(chr.id).grade > 2) {
                Logger.log(LogType.HACK, "Trying to remove a comment without proper permissions", this, c)
                c.close()
                return
            }

            item.deleteComment(comment)
            GuildAPI.deleteBBSComment(bbs.guildId, item.id, comment)

            loadEntry(c, item)
        }
    }
}