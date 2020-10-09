package net.database

import client.Character
import client.Client
import client.player.key.KeyBinding
import database.jooq.Tables.CHARACTERS
import database.jooq.Tables.KEYBINDINGS
import net.database.DatabaseCore.connection
import org.jooq.Record

object CharacterAPI {

    fun getOfflineName(cid: Int): String {
        val rec: Record? = connection
                .select(CHARACTERS.NAME).from(CHARACTERS)
                .where(CHARACTERS.ID.eq(cid)).fetchOne()

        return rec?.getValue(CHARACTERS.NAME) ?: ""
    }

    fun getOfflineId(name: String): Int {
        val rec: Record? = connection
                .select(CHARACTERS.ID).from(CHARACTERS)
                .where(CHARACTERS.NAME.eq(name)).fetchOne()

        return rec?.getValue(CHARACTERS.ID) ?: -1
    }

    fun getOfflineCharacter(cid: Int): Record {
        return connection.select().from(CHARACTERS)
                .where(CHARACTERS.ID.eq(cid))
                .fetchOne()
    }

    fun resetParties() {
        connection.update(CHARACTERS)
                .set(CHARACTERS.PARTY, 0)
                .execute()
    }

    fun getOldPartyId(cid: Int): Int {
        return connection.select(CHARACTERS.PARTY).from(CHARACTERS)
                .where(CHARACTERS.ID.eq(cid)).fetchOne()
                .getValue(CHARACTERS.PARTY)
    }

    fun getNewCharacter(c: Client, id: Int): Character {
        println("start loading $id")
        val record = connection.select().from(CHARACTERS).where(CHARACTERS.ID.eq(id)).fetchOne()
        val name = record.getValue(CHARACTERS.NAME)
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
        connection.update(CHARACTERS)
                .set(CHARACTERS.LEVEL, chr.level)
                .set(CHARACTERS.FACE, chr.face)
                .set(CHARACTERS.HAIR, chr.hair)
                .set(CHARACTERS.SKIN, chr.skinColor)
                .set(CHARACTERS.JOB, chr.job.id)
                .set(CHARACTERS.AP, chr.ap)
                .set(CHARACTERS.SP, chr.sp)
                .set(CHARACTERS.FAME, chr.fame)
                .set(CHARACTERS.MAP, chr.field.forcedReturnMap)
                .set(CHARACTERS.SPAWNPOINT, chr.field.getClosestSpawnpoint(chr.position).id)
                .set(CHARACTERS.STR, chr.strength)
                .set(CHARACTERS.DEX, chr.dexterity)
                .set(CHARACTERS.INT, chr.intelligence)
                .set(CHARACTERS.LUK, chr.luck)
                .set(CHARACTERS.HP, chr.health)
                .set(CHARACTERS.MAX_HP, chr.maxHealth)
                .set(CHARACTERS.MP, chr.mana)
                .set(CHARACTERS.MAX_MP, chr.maxMana)
                .set(CHARACTERS.EXP, chr.exp)
                .set(CHARACTERS.MESO, chr.meso)
                .set(CHARACTERS.PARTY, chr.party?.id ?: 0)
                // todo hardcore, killcount?
                .set(CHARACTERS.TOTAL_DAMAGE, chr.totalDamage)
                .where(CHARACTERS.ID.eq(chr.id))
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
        return connection.select().from(CHARACTERS).where(CHARACTERS.ID.eq(id)).fetchOne()
    }
}