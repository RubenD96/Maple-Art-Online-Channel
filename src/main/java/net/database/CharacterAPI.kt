package net.database

import client.Character
import client.Client
import client.player.Job
import client.player.key.KeyBinding
import database.jooq.Tables
import field.obj.portal.FieldPortal
import net.database.DatabaseCore.connection
import org.jooq.Record

object CharacterAPI {
    fun getOfflineName(cid: Int): String {
        val rec: Record? = connection
                .select(Tables.CHARACTERS.NAME).from(Tables.CHARACTERS)
                .where(Tables.CHARACTERS.ID.eq(cid)).fetchOne()
        return if (rec != null) {
            rec.getValue(Tables.CHARACTERS.NAME)
        } else ""
    }

    fun getOfflineId(name: String?): Int {
        val rec: Record? = connection
                .select(Tables.CHARACTERS.ID).from(Tables.CHARACTERS)
                .where(Tables.CHARACTERS.NAME.eq(name)).fetchOne()
        return if (rec != null) {
            rec.getValue(Tables.CHARACTERS.ID)
        } else -1
    }

    fun getOfflineCharacter(cid: Int): Record {
        return connection
                .select().from(Tables.CHARACTERS)
                .where(Tables.CHARACTERS.ID.eq(cid))
                .fetchOne()
    }

    fun resetParties() {
        connection.update(Tables.CHARACTERS)
                .set(Tables.CHARACTERS.PARTY, 0)
                .execute()
    }

    fun getOldPartyId(cid: Int): Int {
        return connection
                .select(Tables.CHARACTERS.PARTY).from(Tables.CHARACTERS)
                .where(Tables.CHARACTERS.ID.eq(cid)).fetchOne()
                .getValue(Tables.CHARACTERS.PARTY)
    }

    fun getNewCharacter(c: Client, id: Int): Character {
        println("start loading $id")
        val record = connection.select().from(Tables.CHARACTERS).where(Tables.CHARACTERS.ID.eq(id)).fetchOne()
        val name = record.getValue(Tables.CHARACTERS.NAME)
        val charId = record.getValue(Tables.CHARACTERS.ID)
        val gmLevel = record.getValue(Tables.CHARACTERS.GM_LEVEL)
        val level = record.getValue(Tables.CHARACTERS.LEVEL)
        val face = record.getValue(Tables.CHARACTERS.FACE)
        val hair = record.getValue(Tables.CHARACTERS.HAIR)
        val gender = record.getValue(Tables.CHARACTERS.GENDER)
        val skinColor = record.getValue(Tables.CHARACTERS.SKIN)
        val job = Job.getById(record.getValue(Tables.CHARACTERS.JOB))
        val ap = record.getValue(Tables.CHARACTERS.AP)
        val sp = record.getValue(Tables.CHARACTERS.SP)
        val fame = record.getValue(Tables.CHARACTERS.FAME)
        val mapId = record.getValue(Tables.CHARACTERS.MAP)
        val spawnpoint = record.getValue(Tables.CHARACTERS.SPAWNPOINT)
        val str = record.getValue(Tables.CHARACTERS.STR)
        val dex = record.getValue(Tables.CHARACTERS.DEX)
        val intelligence = record.getValue(Tables.CHARACTERS.INT)
        val luck = record.getValue(Tables.CHARACTERS.LUK)
        val hp = record.getValue(Tables.CHARACTERS.HP)
        val mhp = record.getValue(Tables.CHARACTERS.MAX_HP)
        val mp = record.getValue(Tables.CHARACTERS.MP)
        val mmp = record.getValue(Tables.CHARACTERS.MAX_MP)
        val exp = record.getValue(Tables.CHARACTERS.EXP)
        val meso = record.getValue(Tables.CHARACTERS.MESO)
        val character = Character(
                c,
                name,
                charId, gmLevel, level, hair, face,
                gender, skinColor,
                job,
                ap, sp, fame, mapId, spawnpoint,
                str, dex, intelligence, luck,
                hp, mhp, mp, mmp, exp,
                meso
        )
        character.init()
        println("finished loading $name")
        return character
    }

    /**
     * Updates the characters table data except for:
     * - name
     * - gender
     *
     * @param chr character to save
     */
    fun saveCharacterStats(chr: Character) {
        println("start saving " + chr.getName())
        // yikes
        var sp = 0
        val fid: Int
        if (chr.field == null) {
            sp = chr.spawnpoint
            fid = chr.fieldId
        } else {
            val fp: FieldPortal? = chr.field.getClosestSpawnpoint(chr.position)
            if (fp != null) sp = fp.id
            fid = chr.field.forcedReturnMap
        }
        connection.update(Tables.CHARACTERS)
                .set(Tables.CHARACTERS.LEVEL, chr.level)
                .set(Tables.CHARACTERS.FACE, chr.face)
                .set(Tables.CHARACTERS.HAIR, chr.hair)
                .set(Tables.CHARACTERS.SKIN, chr.skinColor)
                .set(Tables.CHARACTERS.JOB, chr.job.id)
                .set(Tables.CHARACTERS.AP, chr.ap)
                .set(Tables.CHARACTERS.SP, chr.sp)
                .set(Tables.CHARACTERS.FAME, chr.fame)
                .set(Tables.CHARACTERS.MAP, fid)
                .set(Tables.CHARACTERS.SPAWNPOINT, sp)
                .set(Tables.CHARACTERS.STR, chr.strength)
                .set(Tables.CHARACTERS.DEX, chr.dexterity)
                .set(Tables.CHARACTERS.INT, chr.intelligence)
                .set(Tables.CHARACTERS.LUK, chr.luck)
                .set(Tables.CHARACTERS.HP, chr.health)
                .set(Tables.CHARACTERS.MAX_HP, chr.maxHealth)
                .set(Tables.CHARACTERS.MP, chr.mana)
                .set(Tables.CHARACTERS.MAX_MP, chr.maxMana)
                .set(Tables.CHARACTERS.EXP, chr.exp)
                .set(Tables.CHARACTERS.MESO, chr.meso)
                .set(Tables.CHARACTERS.PARTY, if (chr.party == null) 0 else chr.party.id)
                .where(Tables.CHARACTERS.ID.eq(chr.id))
                .execute()
        println("finished saving " + chr.getName())
    }

    /**
     * Get bindings that were changed at least once before
     *
     * @param cid character id
     * @return map with KeyBindings and Integer key to bind them to
     */
    fun getKeyBindings(cid: Int): Map<Int, KeyBinding> {
        val keyBindings: MutableMap<Int, KeyBinding> = HashMap()
        val res = connection.select().from(Tables.KEYBINDINGS)
                .where(Tables.KEYBINDINGS.CID.eq(cid))
                .fetch()
        for (rec in res) {
            keyBindings[rec.getValue(Tables.KEYBINDINGS.KEY)] = KeyBinding(rec.getValue(Tables.KEYBINDINGS.TYPE), rec.getValue(Tables.KEYBINDINGS.ACTION))
        }
        return keyBindings
    }

    /**
     * Insert new key bindings, except if binding was already changed before
     *
     * @param chr character
     */
    fun updateKeyBindings(chr: Character) {
        val keyBindings = chr.keyBindings
        for ((key, value) in keyBindings) {
            connection
                    .insertInto(Tables.KEYBINDINGS, Tables.KEYBINDINGS.CID, Tables.KEYBINDINGS.KEY, Tables.KEYBINDINGS.TYPE, Tables.KEYBINDINGS.ACTION)
                    .values(chr.id, key, value.type, value.action)
                    .onDuplicateKeyUpdate()
                    .set(Tables.KEYBINDINGS.TYPE, value.type)
                    .set(Tables.KEYBINDINGS.ACTION, value.action)
                    .where(Tables.KEYBINDINGS.CID.eq(chr.id))
                    .and(Tables.KEYBINDINGS.KEY.eq(key))
                    .execute()
        }
    }

    fun getCharacterInfo(id: Int): Record {
        return connection.select().from(Tables.CHARACTERS).where(Tables.CHARACTERS.ID.eq(id)).fetchOne()
    }
}