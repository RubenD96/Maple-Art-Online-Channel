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
        val dbId = connection.insertInto(QUESTS, QUESTS.QID, QUESTS.CID, QUESTS.STATE)
                .values(quest.id, quest.character.id, quest.state.value)
                .returning(QUESTS.ID)
                .fetchOne().id

        quest.dbId = dbId

        if (quest.mobs.isNotEmpty()) {
            quest.mobs.keys.forEach {
                connection.insertInto(QUESTINFO, QUESTINFO.QID, QUESTINFO.TYPE, QUESTINFO.KEY, QUESTINFO.VALUE)
                        .values(dbId, 1.toByte(), it, "000")
                        .execute()
            }
        }
    }

    fun update(quest: Quest) {
        connection.update(QUESTS)
                .set(QUESTS.STATE, quest.state.value)
                .where(QUESTS.QID.eq(quest.id))
                .and(QUESTS.CID.eq(quest.character.id))
                .execute()

        // dont need this data anymore
        if (quest.state == QuestState.COMPLETE) {
            connection.deleteFrom(QUESTINFO)
                    .where(QUESTINFO.QID.eq(quest.dbId))
                    .execute()
        }
    }

    fun remove(quest: Quest) {
        connection.deleteFrom(QUESTS)
                .where(QUESTS.QID.eq(quest.id))
                .and(QUESTS.CID.eq(quest.character.id))
                .execute()
    }

    fun loadAll(chr: Character) {
        val res = connection.select().from(QUESTS)
                .where(QUESTS.CID.eq(chr.id))
                .fetch()

        res.forEach {
            val quest = Quest(it.getValue(QUESTS.QID), chr)
            val state = it.getValue(QUESTS.STATE)

            if (state == QuestState.PERFORM.value) {
                loadInfo(quest, it.getValue(QUESTS.ID))
            }

            quest.state = if (state.toInt() == 0) QuestState.NONE else if (state.toInt() == 1) QuestState.PERFORM else QuestState.COMPLETE
            quest.dbId = it.getValue(QUESTS.ID)
            chr.quests[quest.id] = quest
        }
    }

    private fun loadInfo(quest: Quest, dbId: Int) {
        val res = connection.select().from(QUESTINFO)
                .where(QUESTINFO.QID.eq(dbId))
                .and(QUESTINFO.TYPE.eq(1.toByte()))
                .fetch()

        res.forEach {
            val mob = it.getValue(QUESTINFO.KEY)
            quest.mobs[mob] = it.getValue(QUESTINFO.VALUE)
            quest.character.registeredQuestMobs.add(mob)
        }
    }

    fun saveInfo(chr: Character) {
        chr.quests.values.stream()
                .filter { it.state == QuestState.PERFORM }
                .forEach { quest ->
                    quest.mobs.forEach {
                        connection.update(QUESTINFO)
                                .set(QUESTINFO.VALUE, it.value)
                                .where(QUESTINFO.KEY.eq(it.key))
                                .execute()
                    }
                }
    }
}