package net.database;

import client.Character;
import client.player.quest.Quest;
import client.player.quest.QuestState;
import org.jooq.Record;
import org.jooq.Result;

import static database.jooq.Tables.QUESTS;

public class QuestAPI {

    /**
     * Starting a new quest that has never been started before (probably always QuestState.PERFORM)
     *
     * @param quest object
     */
    public static void register(Quest quest) {
        DatabaseCore.getConnection()
                .insertInto(QUESTS, QUESTS.QID, QUESTS.CID, QUESTS.STATE)
                .values(quest.getId(), quest.getCharacter().getId(), (byte) quest.getState().getValue())
                .execute();
    }

    public static void update(Quest quest) {
        DatabaseCore.getConnection()
                .update(QUESTS)
                .set(QUESTS.STATE, (byte) quest.getState().getValue())
                .where(QUESTS.QID.eq(quest.getId()))
                .and(QUESTS.CID.eq(quest.getCharacter().getId()))
                .execute();
    }

    public static void remove(Quest quest) {
        DatabaseCore.getConnection()
                .deleteFrom(QUESTS)
                .where(QUESTS.QID.eq(quest.getId()))
                .and(QUESTS.CID.eq(quest.getCharacter().getId()))
                .execute();
    }

    public static void loadAll(Character chr) {
        Result<Record> res = DatabaseCore.getConnection()
                .select().from(QUESTS)
                .where(QUESTS.CID.eq(chr.getId()))
                .fetch();

        res.forEach(rec -> {
            Quest quest = new Quest(rec.getValue(QUESTS.QID), chr);
            byte state = rec.getValue(QUESTS.STATE);
            quest.setState(state == 0 ? QuestState.NONE : (state == 1 ? QuestState.PERFORM : QuestState.COMPLETE));
            chr.getQuests().put(quest.getId(), quest);
        });
    }
}
