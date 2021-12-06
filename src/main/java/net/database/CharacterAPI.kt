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

object CharacterAPI {

    fun getOfflineName(cid: Int): String {
        with(CHARACTERS) {
            val rec: Record? = connection
                .select(NAME).from(this)
                .where(ID.eq(cid)).fetchOne()

            return rec?.getValue(NAME) ?: ""
        }
    }

    fun getOfflineId(name: String): Int {
        with(CHARACTERS) {
            val rec: Record? = connection
                .select(ID).from(this)
                .where(NAME.eq(name)).fetchOne()

            return rec?.getValue(ID) ?: -1
        }
    }

    fun getOfflineCharacter(cid: Int): Record {
        with(CHARACTERS) {
            return connection.select().from(this)
                .where(ID.eq(cid))
                .fetchOne()
        }
    }

    fun resetParties() {
        with(CHARACTERS) {
            connection.update(this)
                .set(PARTY, 0)
                .execute()
        }
    }

    fun getOldPartyId(cid: Int): Int {
        with(CHARACTERS) {
            return connection.select(PARTY).from(this)
                .where(ID.eq(cid)).fetchOne()
                .getValue(PARTY)
        }
    }

    fun getNewCharacter(c: Client, id: Int): Character {
        with(CHARACTERS) {
            println("start loading $id")
            val record = connection.select().from(this).where(ID.eq(id)).fetchOne()
            val name = record.getValue(NAME)
            val character = Character(c, name, record)
            println("finished loading $name")
            return character
        }
    }

    /**
     * Updates the characters table data except for:
     * - name
     * - gender
     *
     * @param chr character to save
     */
    fun saveCharacterStats(chr: Character) {
        with(CHARACTERS) {
            //println("start saving " + chr.name)
            connection.update(this)
                .set(LEVEL, chr.level)
                .set(FACE, chr.face)
                .set(HAIR, chr.hair)
                .set(SKIN, chr.skinColor)
                .set(JOB, chr.job.id)
                .set(AP, chr.ap)
                //.set(CHARACTERS.SP, chr.curSp)
                .set(FAME, chr.fame)
                .set(MAP, chr.field.template.forcedReturnMap)
                .set(SPAWNPOINT, chr.field.getClosestSpawnpoint(chr.position).id)
                .set(STR, chr.strength)
                .set(DEX, chr.dexterity)
                .set(INT, chr.intelligence)
                .set(LUK, chr.luck)
                .set(HP, chr.health)
                .set(MAX_HP, chr.maxHealth)
                .set(MP, chr.mana)
                .set(MAX_MP, chr.maxMana)
                .set(EXP, chr.exp)
                .set(MESO, chr.meso)
                .set(PARTY, chr.party?.id ?: 0)
                .set(KILL_COUNT, chr.mobKills.values.sum())
                .set(TOTAL_DAMAGE, chr.totalDamage)
                .where(ID.eq(chr.id))
                .execute()
            //println("finished saving " + chr.name)
        }
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
        with(KEYBINDINGS) {
            val keyBindings: MutableMap<Int, KeyBinding> = HashMap()
            val res = connection.select().from(this)
                .where(CID.eq(cid))
                .fetch()

            res.forEach {
                keyBindings[it.getValue(KEY)] =
                    KeyBinding(it.getValue(TYPE), it.getValue(ACTION))
            }

            return keyBindings
        }
    }

    /**
     * Insert new key bindings, except if binding was already changed before
     *
     * @param chr character
     */
    fun updateKeyBindings(chr: Character) {
        with(KEYBINDINGS) {
            val keyBindings = HashMap(chr.keyBindings)
            keyBindings.forEach {
                connection.insertInto(this, CID, KEY, TYPE, ACTION)
                    .values(chr.id, it.key, it.value.type, it.value.action)
                    .onDuplicateKeyUpdate()
                    .set(TYPE, it.value.type)
                    .set(ACTION, it.value.action)
                    .where(CID.eq(chr.id))
                    .and(KEY.eq(it.key))
                    .execute()
            }
        }
    }

    /**
     * Get the current skills of the player
     *
     * @param cid character id
     * @return map with Skills
     */
    fun getSkills(cid: Int): MutableMap<Int, Skill> {
        with(SKILLS) {
            val skills: MutableMap<Int, Skill> = HashMap()
            val res = connection.select().from(this)
                .where(CID.eq(cid))
                .fetch()

            res.forEach {
                val skill = Skill(it.getValue(LEVEL))
                skill.masterLevel = it.getValue(MASTER_LEVEL)
                skill.expire = it.getValue(EXPIRE)
                skills[it.getValue(SKILL)] = skill
            }

            return skills
        }
    }

    /**
     * Insert new skill record, except if skill was already leveled before
     *
     * @param chr character
     */
    fun updateSkills(chr: Character) {
        with(SKILLS) {
            val skills = chr.skills
            skills.forEach {
                connection.insertInto(this, CID, SKILL, LEVEL, MASTER_LEVEL, EXPIRE)
                    .values(chr.id, it.key, it.value.level, it.value.masterLevel, it.value.expire)
                    .onDuplicateKeyUpdate()
                    .set(LEVEL, it.value.level)
                    .set(MASTER_LEVEL, it.value.masterLevel)
                    .set(EXPIRE, it.value.expire)
                    .where(CID.eq(chr.id))
                    .and(SKILL.eq(it.key))
                    .execute()
            }
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
        with(MOBKILLS) {
            val mobKills: MutableMap<Int, Int> = HashMap()
            val res = connection.select().from(this)
                .where(CID.eq(cid))
                .fetch()

            res.forEach {
                mobKills[it.getValue(MID)] = it.getValue(COUNT)
            }

            return mobKills
        }
    }

    fun saveMobKills(chr: Character) {
        with(MOBKILLS) {
            chr.killedMobs.forEach {
                connection.insertInto(this, CID, MID, COUNT)
                    .values(chr.id, it, chr.mobKills[it])
                    .onDuplicateKeyUpdate()
                    .set(COUNT, chr.mobKills[it])
                    .where(CID.eq(chr.id))
                    .and(MID.eq(it))
                    .execute()
            }
        }
    }

    fun getCharacterInfo(id: Int): Record {
        with(CHARACTERS) {
            return connection.select().from(this).where(ID.eq(id)).fetchOne()
        }
    }
}