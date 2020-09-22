package net.maple.packets

import net.maple.SendOpcode
import scripting.npc.ConversationType
import util.packet.Packet
import util.packet.PacketWriter

object ConversationPackets {

    private fun getMessagePacket(npc: Int, type: ConversationType, speaker: Int, text: String): PacketWriter {
        val pw = PacketWriter(16)

        pw.writeHeader(SendOpcode.SCRIPT_MESSAGE)
        pw.write(0) // SpeakerTypeID
        pw.writeInt(npc)
        pw.write(type.value)
        pw.write(speaker)
        pw.writeMapleString(text)

        return pw
    }

    private fun getSayMessagePacket(npc: Int, speaker: Int, text: String, prev: Boolean, next: Boolean): Packet {
        val pw = getMessagePacket(npc, ConversationType.SAY, speaker, text)

        pw.writeBool(prev)
        pw.writeBool(next)

        return pw.createPacket()
    }

    fun getOkMessagePacket(npc: Int, speaker: Int, text: String): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = false, next = false)
    }

    fun getPrevMessagePacket(npc: Int, speaker: Int, text: String): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = true, next = false)
    }

    fun getNextMessagePacket(npc: Int, speaker: Int, text: String): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = false, next = true)
    }

    fun getNextPrevMessagePacket(npc: Int, speaker: Int, text: String): Packet {
        return getSayMessagePacket(npc, speaker, text, prev = true, next = true)
    }

    fun getYesNoMessagePacket(npc: Int, speaker: Int, text: String): Packet {
        return getMessagePacket(npc, ConversationType.ASK_YES_NO, speaker, text).createPacket()
    }

    fun getTextMessagePacket(npc: Int, speaker: Int, text: String, def: String, min: Int, max: Int): Packet {
        val pw = getMessagePacket(npc, ConversationType.ASK_TEXT, speaker, text)

        pw.writeMapleString(def)
        pw.writeShort(min)
        pw.writeShort(max)

        return pw.createPacket()
    }

    fun getNumberMessagePacket(npc: Int, speaker: Int, text: String, def: Int, min: Int, max: Int): Packet {
        val pw = getMessagePacket(npc, ConversationType.ASK_NUMBER, speaker, text)

        pw.writeInt(def)
        pw.writeInt(min)
        pw.writeInt(max)

        return pw.createPacket()
    }

    fun getSimpleMessagePacket(npc: Int, speaker: Int, text: String): Packet {
        return getMessagePacket(npc, ConversationType.ASK_MENU, speaker, text).createPacket()
    }

    fun getAcceptMessagePacket(npc: Int, speaker: Int, text: String): Packet {
        return getMessagePacket(npc, ConversationType.ASK_ACCEPT, speaker, text).createPacket()
    }

    fun getBoxTextMessagePacket(npc: Int, speaker: Int, def: String, cols: Int, rows: Int): Packet {
        val pw = getMessagePacket(npc, ConversationType.ASK_BOX_TEXT, speaker, "")

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