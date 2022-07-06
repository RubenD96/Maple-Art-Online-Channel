package net.maple.packets

import net.maple.SendOpcode
import scripting.dialog.ConversationType
import scripting.dialog.SpeakerType
import util.packet.Packet
import util.packet.PacketWriter

object ConversationPackets {

    private fun getMessagePacket(npc: Int, type: ConversationType, speaker: Int, text: String, replaceNpc: Int?): PacketWriter {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.SCRIPT_MESSAGE)
        pw.write(0) // SpeakerTypeID
        pw.writeInt(npc)
        pw.write(type.value)
        pw.write(speaker)

        if (speaker and SpeakerType.NpcReplacedByNpc == SpeakerType.NpcReplacedByNpc) {
            pw.writeInt(replaceNpc ?: npc)
        }

        pw.writeMapleString(text)

        return pw
    }

    private fun getSayMessagePacket(npc: Int, speaker: Int, text: String, prev: Boolean, next: Boolean, replaceNpc: Int?): Packet {
        val pw = getMessagePacket(npc, ConversationType.SAY, speaker, text, replaceNpc)

        pw.writeBool(prev)
        pw.writeBool(next)

        return pw.createPacket()
    }

    fun getOkMessagePacket(npc: Int, speaker: Int, text: String, replaceNpc: Int? = null): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = false, next = false, replaceNpc)
    }

    fun getPrevMessagePacket(npc: Int, speaker: Int, text: String, replaceNpc: Int? = null): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = true, next = false, replaceNpc)
    }

    fun getNextMessagePacket(npc: Int, speaker: Int, text: String, replaceNpc: Int? = null): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = false, next = true, replaceNpc)
    }

    fun getNextPrevMessagePacket(npc: Int, speaker: Int, text: String, replaceNpc: Int? = null): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = true, next = true, replaceNpc)
    }

    fun getYesNoMessagePacket(npc: Int, speaker: Int, text: String, replaceNpc: Int? = null): Packet {
        return getMessagePacket(npc, ConversationType.ASK_YES_NO, speaker, text, replaceNpc).createPacket()
    }

    fun getTextMessagePacket(npc: Int, speaker: Int, text: String, def: String, min: Int, max: Int, replaceNpc: Int? = null): Packet {
        val pw = getMessagePacket(npc, ConversationType.ASK_TEXT, speaker, text, replaceNpc)

        pw.writeMapleString(def)
        pw.writeShort(min)
        pw.writeShort(max)

        return pw.createPacket()
    }

    fun getNumberMessagePacket(npc: Int, speaker: Int, text: String, def: Int, min: Int, max: Int, replaceNpc: Int? = null): Packet {
        val pw = getMessagePacket(npc, ConversationType.ASK_NUMBER, speaker, text, replaceNpc)

        pw.writeInt(def)
        pw.writeInt(min)
        pw.writeInt(max)

        return pw.createPacket()
    }

    fun getSimpleMessagePacket(npc: Int, speaker: Int, text: String, replaceNpc: Int? = null): Packet {
        return getMessagePacket(npc, ConversationType.ASK_MENU, speaker, text, replaceNpc).createPacket()
    }

    fun getAcceptMessagePacket(npc: Int, speaker: Int, text: String, replaceNpc: Int? = null): Packet {
        return getMessagePacket(npc, ConversationType.ASK_ACCEPT, speaker, text, replaceNpc).createPacket()
    }

    fun getBoxTextMessagePacket(npc: Int, speaker: Int, def: String, cols: Int, rows: Int, replaceNpc: Int? = null): Packet {
        val pw = getMessagePacket(npc, ConversationType.ASK_BOX_TEXT, speaker, "", replaceNpc)

        pw.writeMapleString(def)
        pw.writeShort(cols)
        pw.writeShort(rows)

        return pw.createPacket()
    }

    /**
     * Very weird one, dimensional portal in gms I think
     */
    fun getSlideMenuMessagePacket(npc: Int, speaker: Int, text: String, type: Int, selected: Int): Packet {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.SCRIPT_MESSAGE)
        pw.write(0) // SpeakerTypeID
        pw.writeInt(npc)
        pw.write(ConversationType.ASK_SLIDE_MENU.value)
        pw.write(speaker)
        pw.writeInt(type)
        pw.writeInt(selected)
        pw.writeMapleString(text)

        return pw.createPacket()
    }
}