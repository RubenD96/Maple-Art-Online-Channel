package world.guild

import client.Character
import database.jooq.Tables
import database.jooq.Tables.CHARACTERS
import database.jooq.Tables.GUILDMEMBERS
import net.database.CharacterAPI.getCharacterInfo
import net.server.Server
import org.jooq.Record
import util.packet.PacketWriter

class GuildMember {

    var name: String
    var job = 0
    var level = 0
    var grade: Int
    var commitment: Int
    var allianceGrade: Int
    var isOnline = false
    var character: Character? = null

    /**
     * Used for in-game guild invites
     */
    constructor(chr: Character) {
        name = chr.name
        job = chr.job.id
        level = chr.level
        grade = 5
        commitment = 0
        allianceGrade = 5
        isOnline = true
        character = chr
    }

    /**
     * Used for DB load
     *
     * @param rec SQL info
     */
    constructor(rec: Record) {
        val id = rec.getValue(GUILDMEMBERS.CID)

        val chr = Server.getCharacter(id)
        if (chr != null) {
            name = chr.name
            job = chr.job.id
            level = chr.level
            isOnline = true
            character = chr
        } else {
            val info = getCharacterInfo(id)
            name = info.getValue(CHARACTERS.NAME)
            job = info.getValue(CHARACTERS.JOB)
            level = info.getValue(CHARACTERS.LEVEL)
            isOnline = false
        }

        grade = rec.getValue(GUILDMEMBERS.GUILDGRADE).toInt()
        commitment = 0
        allianceGrade = rec.getValue(GUILDMEMBERS.ALLIANCEGRADE).toInt()
    }

    fun encode(pw: PacketWriter) {
        pw.writeFixedString(name, 13)
        pw.writeInt(job)
        pw.writeInt(level)
        pw.writeInt(grade)
        pw.writeInt(if (isOnline) 1 else 0)
        pw.writeInt(commitment)
        pw.writeInt(allianceGrade)
    }

    /**
     * For use of method reference
     *
     * @return True if character is set, false if not
     */
    fun hasCharacter(): Boolean {
        return character != null
    }
}