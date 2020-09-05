package net.database

import client.Character
import client.player.quest.Quest
import client.player.quest.QuestState
import database.jooq.Tables
import net.database.DatabaseCore.connection
import org.jooq.Record

object QuestAPI {
    /**
     * Starting a new quest that has never been started before (probably always QuestState.PERFORM)
     *
     * @param quest object
     */
    fun register(quest: Quest) {
        val dbId = connection
                .insertInto(Tables.QUESTS, Tables.QUESTS.QID, Tables.QUESTS.CID, Tables.QUESTS.STATE)
                .values(quest.id, quest.character.id, quest.state.value.toByte())
                .returning(Tables.QUESTS.ID)
                .fetchOne().id
        quest.dbId = dbId
        if (!quest.mobs.isEmpty()) {
            quest.mobs.keys.forEach { mob: Int ->
                connection
                        .insertInto(Tables.QUESTINFO, Tables.QUESTINFO.QID, Tables.QUESTINFO.TYPE, Tables.QUESTINFO.KEY, Tables.QUESTINFO.VALUE)
                        .values(dbId, 1.toByte(), mob, "000")
                        .execute()
            }
        }
    }

    fun update(quest: Quest) {
        connection
                .update(Tables.QUESTS)
                .set(Tables.QUESTS.STATE, quest.state.value.toByte())
                .where(Tables.QUESTS.QID.eq(quest.id))
                .and(Tables.QUESTS.CID.eq(quest.character.id))
                .execute()

        // dont need this data anymore
        if (quest.state == QuestState.COMPLETE) {
            connection
                    .deleteFrom(Tables.QUESTINFO)
                    .where(Tables.QUESTINFO.QID.eq(quest.dbId))
                    .execute()
        }
    }

    fun remove(quest: Quest) {
        connection
                .deleteFrom(Tables.QUESTS)
                .where(Tables.QUESTS.QID.eq(quest.id))
                .and(Tables.QUESTS.CID.eq(quest.character.id))
                .execute()
    }

    fun loadAll(chr: Character) {
        val res = connection
                .select().from(Tables.QUESTS)
                .where(Tables.QUESTS.CID.eq(chr.id))
                .fetch()
        res.forEach { rec: Record ->
            val quest = Quest(rec.getValue(Tables.QUESTS.QID), chr)
            val state = rec.getValue(Tables.QUESTS.STATE)
            if (state.toInt() == QuestState.PERFORM.value) {
                loadInfo(quest, rec.getValue(Tables.QUESTS.ID))
            }
            quest.state = if (state.toInt() == 0) QuestState.NONE else if (state.toInt() == 1) QuestState.PERFORM else QuestState.COMPLETE
            quest.dbId = rec.getValue(Tables.QUESTS.ID)
            chr.quests[quest.id] = quest
        }
    }

    private fun loadInfo(quest: Quest, dbId: Int) {
        val res = connection
                .select().from(Tables.QUESTINFO)
                .where(Tables.QUESTINFO.QID.eq(dbId))
                .and(Tables.QUESTINFO.TYPE.eq(1.toByte()))
                .fetch()
        res.forEach { rec: Record ->
            val mob = rec.getValue(Tables.QUESTINFO.KEY)
            quest.mobs[mob] = rec.getValue(Tables.QUESTINFO.VALUE)
            quest.character.registeredQuestMobs.add(mob)
        }
    }

    fun saveInfo(chr: Character) {
        chr.quests.values.stream()
                .filter { quest: Quest -> quest.state == QuestState.PERFORM }
                .forEach { quest: Quest ->
                    quest.mobs.forEach { (mob: Int?, count: String) ->
                        connection
                                .update(Tables.QUESTINFO)
                                .set(Tables.QUESTINFO.VALUE, count)
                                .where(Tables.QUESTINFO.KEY.eq(mob))
                                .execute()
                    }
                }
    }
}