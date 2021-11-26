package net.database

import client.Character
import client.player.quest.Quest
import client.player.quest.QuestState
import database.jooq.Tables.QUESTINFO
import database.jooq.Tables.QUESTS
import net.database.DatabaseCore.connection

object QuestAPI {

    /**
     * Starting a new quest that has never been started before (probably always QuestState.PERFORM)
     *
     * @param quest object
     */
    fun register(quest: Quest) {
        with(QUESTS) {
            val dbId = connection.insertInto(this, QID, CID, STATE)
                .values(quest.id, quest.character.id, quest.state.value)
                .returning(ID)
                .fetchOne().id

            quest.dbId = dbId

            with(QUESTINFO) {
                if (quest.mobs.isNotEmpty()) {
                    quest.mobs.keys.forEach {
                        connection.insertInto(this, QID, TYPE, KEY, VALUE)
                            .values(dbId, 1.toByte(), it, "000")
                            .execute()
                    }
                }
            }
        }
    }

    fun update(quest: Quest) {
        with(QUESTS) {
            connection.update(this)
                .set(STATE, quest.state.value)
                .where(QID.eq(quest.id))
                .and(CID.eq(quest.character.id))
                .execute()
        }

        // dont need this data anymore
        with(QUESTINFO) {
            if (quest.state == QuestState.COMPLETE) {
                connection.deleteFrom(this)
                    .where(QID.eq(quest.dbId))
                    .execute()
            }
        }
    }

    fun remove(quest: Quest) {
        with(QUESTS) {
            connection.deleteFrom(this)
                .where(QID.eq(quest.id))
                .and(CID.eq(quest.character.id))
                .execute()
        }
    }

    fun loadAll(chr: Character) {
        with(QUESTS) {
            val res = connection.select().from(this)
                .where(CID.eq(chr.id))
                .fetch()

            res.forEach {
                val quest = Quest(it.getValue(QID), chr)
                val state = it.getValue(STATE)

                if (state == QuestState.PERFORM.value) {
                    loadInfo(quest, it.getValue(ID))
                }

                quest.state =
                    if (state.toInt() == 0) QuestState.NONE else if (state.toInt() == 1) QuestState.PERFORM else QuestState.COMPLETE
                quest.dbId = it.getValue(ID)
                chr.quests[quest.id] = quest
            }
        }
    }

    private fun loadInfo(quest: Quest, dbId: Int) {
        with(QUESTINFO) {
            val res = connection.select().from(this)
                .where(QID.eq(dbId))
                .and(TYPE.eq(1.toByte()))
                .fetch()

            res.forEach {
                val mob = it.getValue(KEY)
                quest.mobs[mob] = it.getValue(VALUE)
                quest.character.registeredQuestMobs.add(mob)
            }
        }
    }

    fun saveInfo(chr: Character) {
        with(QUESTINFO) {
            chr.quests.values
                .filter { it.state == QuestState.PERFORM }
                .forEach { quest ->
                    quest.mobs.forEach {
                        connection.update(this)
                            .set(VALUE, it.value)
                            .where(KEY.eq(it.key))
                            .execute()
                    }
                }
        }
    }
}