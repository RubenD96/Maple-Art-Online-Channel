package net.database

import client.Character
import client.Client
import client.player.key.KeyBinding
import database.jooq.Tables
import database.jooq.Tables.KEYBINDINGS
import net.database.DatabaseCore.connection
import org.jooq.Record

object CharacterAPI {

    fun getOfflineName(cid: Int): String {
        val rec: Record? = connection
                .select(Tables.CHARACTERS.NAME).from(Tables.CHARACTERS)
                .where(Tables.CHARACTERS.ID.eq(cid)).fetchOne()
        return rec?.let { rec.getValue(Tables.CHARACTERS.NAME) } ?: ""
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
        val character = Character(c, name, record)
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
        println("start saving " + chr.name)
        connection.update(Tables.CHARACTERS)
                .set(Tables.CHARACTERS.LEVEL, chr.level)
                .set(Tables.CHARACTERS.FACE, chr.face)
                .set(Tables.CHARACTERS.HAIR, chr.hair)
                .set(Tables.CHARACTERS.SKIN, chr.skinColor)
                .set(Tables.CHARACTERS.JOB, chr.job.id)
                .set(Tables.CHARACTERS.AP, chr.ap)
                .set(Tables.CHARACTERS.SP, chr.sp)
                .set(Tables.CHARACTERS.FAME, chr.fame)
                .set(Tables.CHARACTERS.MAP, chr.field.forcedReturnMap)
                .set(Tables.CHARACTERS.SPAWNPOINT, chr.field.getClosestSpawnpoint(chr.position).id)
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
                .set(Tables.CHARACTERS.PARTY, chr.party?.id ?: 0)
                .where(Tables.CHARACTERS.ID.eq(chr.id))
                .execute()
        println("finished saving " + chr.name)
    }

    /**
     * Get bindings that were changed at least once before
     *
     * @param cid character id
     * @return map with KeyBindings and Integer key to bind them to
     */
    fun getKeyBindings(cid: Int): MutableMap<Int, KeyBinding> {
        val keyBindings: MutableMap<Int, KeyBinding> = HashMap()
        val res = connection.select().from(KEYBINDINGS)
                .where(KEYBINDINGS.CID.eq(cid))
                .fetch()
        res.forEach {
            keyBindings[it.getValue(KEYBINDINGS.KEY)] = KeyBinding(it.getValue(KEYBINDINGS.TYPE), it.getValue(KEYBINDINGS.ACTION))
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
        keyBindings.forEach {
            connection.insertInto(KEYBINDINGS, KEYBINDINGS.CID, KEYBINDINGS.KEY, KEYBINDINGS.TYPE, KEYBINDINGS.ACTION)
                    .values(chr.id, it.key, it.value.type, it.value.action)
                    .onDuplicateKeyUpdate()
                    .set(KEYBINDINGS.TYPE, it.value.type)
                    .set(KEYBINDINGS.ACTION, it.value.action)
                    .where(KEYBINDINGS.CID.eq(chr.id))
                    .and(KEYBINDINGS.KEY.eq(it.key))
                    .execute()
        }
    }

    fun getCharacterInfo(id: Int): Record {
        return connection.select().from(Tables.CHARACTERS).where(Tables.CHARACTERS.ID.eq(id)).fetchOne()
    }
}