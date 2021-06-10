package net.database

import client.Character
import client.Client
import client.player.Skill
import client.player.WeaponType
import client.player.key.KeyBinding
import database.jooq.Tables.*
import net.database.DatabaseCore.connection
import org.jooq.Record
import skill.Macro
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
        //println("start saving " + chr.name)
        connection.update(CHARACTERS)
            .set(CHARACTERS.LEVEL, chr.level)
            .set(CHARACTERS.FACE, chr.face)
            .set(CHARACTERS.HAIR, chr.hair)
            .set(CHARACTERS.SKIN, chr.skinColor)
            .set(CHARACTERS.JOB, chr.job.id)
            .set(CHARACTERS.AP, chr.ap)
            //.set(CHARACTERS.SP, chr.curSp)
            .set(CHARACTERS.FAME, chr.fame)
            .set(CHARACTERS.MAP, chr.field.template.forcedReturnMap)
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
            .set(CHARACTERS.KILL_COUNT, chr.mobKills.values.sum())
            .set(CHARACTERS.TOTAL_DAMAGE, chr.totalDamage)
            .where(CHARACTERS.ID.eq(chr.id))
            .execute()
        //println("finished saving " + chr.name)
    }

    /**
     * Get weapon sp that were changed at least once before
     *
     * @param cid character id
     * @return map with WeaponType and Integer amount
     */
    fun getWeaponSp(cid: Int): MutableMap<WeaponType, Int> {
        val sp: MutableMap<WeaponType, Int> = EnumMap(WeaponType::class.java)

        with(SKILLPOINTS) {
            val res = connection.select().from(this)
                .where(CID.eq(cid))
                .fetch()

            res.forEach {
                sp[WeaponType.getById(it.getValue(TYPE))] = it.getValue(AMOUNT)
            }
        }

        return sp
    }

    /**
     * Updates the sp per weapon type
     *
     * @param chr character to save
     */
    fun updateWeaponSp(chr: Character) {
        with(SKILLPOINTS) {
            val sp = EnumMap(chr.skillpoints)
            sp.forEach {
                connection.insertInto(this, CID, TYPE, AMOUNT)
                    .values(chr.id, it.key.type, it.value)
                    .onDuplicateKeyUpdate()
                    .set(AMOUNT, it.value)
                    .where(CID.eq(chr.id))
                    .and(TYPE.eq(it.key.type))
                    .execute()
            }
        }
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
            keyBindings[it.getValue(KEYBINDINGS.KEY)] =
                KeyBinding(it.getValue(KEYBINDINGS.TYPE), it.getValue(KEYBINDINGS.ACTION))
        }

        return keyBindings
    }

    /**
     * Insert new key bindings, except if binding was already changed before
     *
     * @param chr character
     */
    fun updateKeyBindings(chr: Character) {
        val keyBindings = HashMap(chr.keyBindings)
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

    /**
     * Get the current skills of the player
     *
     * @param cid character id
     * @return map with Skills
     */
    fun getSkills(cid: Int): MutableMap<Int, Skill> {
        val skills: MutableMap<Int, Skill> = HashMap()
        val res = connection.select().from(SKILLS)
            .where(SKILLS.CID.eq(cid))
            .fetch()

        res.forEach {
            val skill = Skill(it.getValue(SKILLS.LEVEL))
            skill.masterLevel = it.getValue(SKILLS.MASTER_LEVEL)
            skill.expire = it.getValue(SKILLS.EXPIRE)
            skills[it.getValue(SKILLS.SKILL)] = skill
        }

        return skills
    }

    /**
     * Insert new skill record, except if skill was already leveled before
     *
     * @param chr character
     */
    fun updateSkills(chr: Character) {
        val skills = chr.skills
        skills.forEach {
            connection.insertInto(SKILLS, SKILLS.CID, SKILLS.SKILL, SKILLS.LEVEL, SKILLS.MASTER_LEVEL, SKILLS.EXPIRE)
                .values(chr.id, it.key, it.value.level, it.value.masterLevel, it.value.expire)
                .onDuplicateKeyUpdate()
                .set(SKILLS.LEVEL, it.value.level)
                .set(SKILLS.MASTER_LEVEL, it.value.masterLevel)
                .set(SKILLS.EXPIRE, it.value.expire)
                .where(SKILLS.CID.eq(chr.id))
                .and(SKILLS.SKILL.eq(it.key))
                .execute()
        }
    }

    fun getMacros(cid: Int): MutableMap<WeaponType, MutableList<Macro>> {
        val macros: MutableMap<WeaponType, MutableList<Macro>> = EnumMap(WeaponType::class.java)
        with(MACROS) {
            val res = connection.select().from(this)
                .where(CID.eq(cid))
                .orderBy(NUM)
                .fetch()

            res.forEach {
                val macro = Macro(
                    it.getValue(NAME),
                    it.getValue(SHOUT) == 1.toByte(),
                    intArrayOf(
                        it.getValue(SKILL1),
                        it.getValue(SKILL2),
                        it.getValue(SKILL3)
                    )
                )

                val type = WeaponType.getById(it.getValue(TYPE))
                macros.computeIfAbsent(type) { ArrayList() }.add(macro)
            }
        }

        return macros
    }

    fun updateMacros(chr: Character) {
        val macros = chr.macros
        with(MACROS) {
            macros.forEach { (type, list) ->
                list.forEachIndexed() { index, macro ->
                    connection.insertInto(
                        this,
                        CID,
                        TYPE,
                        NUM,
                        SHOUT,
                        NAME,
                        SKILL1,
                        SKILL2,
                        SKILL3
                    ).values(
                        chr.id,
                        type.type,
                        index,
                        if (macro.shout) 1 else 0,
                        macro.name,
                        macro.skills[0],
                        macro.skills[1],
                        macro.skills[2]
                    ).onDuplicateKeyUpdate()
                        .set(SHOUT, if (macro.shout) 1 else 0)
                        .set(NAME, macro.name)
                        .set(SKILL1, macro.skills[0])
                        .set(SKILL2, macro.skills[1])
                        .set(SKILL3, macro.skills[2])
                        .where(CID.eq(chr.id))
                        .and(TYPE.eq(type.type))
                        .and(NUM.eq(index))
                        .execute()
                }
            }
        }
    }

    fun getMobKills(cid: Int): MutableMap<Int, Int> {
        val mobKills: MutableMap<Int, Int> = HashMap()
        val res = connection.select().from(MOBKILLS)
            .where(MOBKILLS.CID.eq(cid))
            .fetch()

        res.forEach {
            mobKills[it.getValue(MOBKILLS.MID)] = it.getValue(MOBKILLS.COUNT)
        }

        return mobKills
    }

    fun saveMobKills(chr: Character) {
        chr.killedMobs.forEach {
            connection.insertInto(MOBKILLS, MOBKILLS.CID, MOBKILLS.MID, MOBKILLS.COUNT)
                .values(chr.id, it, chr.mobKills[it])
                .onDuplicateKeyUpdate()
                .set(MOBKILLS.COUNT, chr.mobKills[it])
                .where(MOBKILLS.CID.eq(chr.id))
                .and(MOBKILLS.MID.eq(it))
                .execute()
        }
    }

    fun getCharacterInfo(id: Int): Record {
        return connection.select().from(CHARACTERS).where(CHARACTERS.ID.eq(id)).fetchOne()
    }
}