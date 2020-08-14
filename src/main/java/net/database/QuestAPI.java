package net.database;

import client.Character;
import client.player.quest.Quest;
import client.player.quest.QuestState;
import org.jooq.Record;
import org.jooq.Result;

import static database.jooq.Tables.QUESTINFO;
import static database.jooq.Tables.QUESTS;

public class QuestAPI {

    /**
     * Starting a new quest that has never been started before (probably always QuestState.PERFORM)
     *
     * @param quest object
     */
    public static void register(Quest quest) {
        int dbId = DatabaseCore.getConnection()
                .insertInto(QUESTS, QUESTS.QID, QUESTS.CID, QUESTS.STATE)
                .values(quest.getId(), quest.getCharacter().getId(), (byte) quest.getState().getValue())
                .returning(QUESTS.ID)
                .fetchOne().getId();
        quest.setDbId(dbId);

        if (!quest.getMobs().isEmpty()) {
            quest.getMobs().keySet().forEach(mob -> {
                DatabaseCore.getConnection()
                        .insertInto(QUESTINFO, QUESTINFO.QID, QUESTINFO.TYPE, QUESTINFO.KEY, QUESTINFO.VALUE)
                        .values(dbId, (byte) 1, mob, "000")
                        .execute();
            });
        }
    }

    public static void update(Quest quest) {
        DatabaseCore.getConnection()
                .update(QUESTS)
                .set(QUESTS.STATE, (byte) quest.getState().getValue())
                .where(QUESTS.QID.eq(quest.getId()))
                .and(QUESTS.CID.eq(quest.getCharacter().getId()))
                .execute();

        // dont need this data anymore
        if (quest.getState() == QuestState.COMPLETE) {
            DatabaseCore.getConnection()
                    .deleteFrom(QUESTINFO)
                    .where(QUESTINFO.QID.eq(quest.getDbId()))
                    .execute();
        }
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
            if (state == QuestState.PERFORM.getValue()) {
                loadInfo(quest, rec.getValue(QUESTS.ID));
            }
            quest.setState(state == 0 ? QuestState.NONE : (state == 1 ? QuestState.PERFORM : QuestState.COMPLETE));
            quest.setDbId(rec.getValue(QUESTS.ID));
            chr.getQuests().put(quest.getId(), quest);
        });
    }

    private static void loadInfo(Quest quest, int dbId) {
        Result<Record> res = DatabaseCore.getConnection()
                .select().from(QUESTINFO)
                .where(QUESTINFO.QID.eq(dbId))
                .and(QUESTINFO.TYPE.eq((byte) 1))
                .fetch();

        res.forEach(rec -> {
            int mob = rec.getValue(QUESTINFO.KEY);
            quest.getMobs().put(mob, rec.getValue(QUESTINFO.VALUE));
            quest.getCharacter().getRegisteredQuestMobs().add(mob);
        });
    }

    public static void saveInfo(Character chr) {
        chr.getQuests().values().stream()
                .filter(quest -> quest.getState() == QuestState.PERFORM)
                .forEach(quest -> {
                    quest.getMobs().forEach((mob, count) -> {
                        DatabaseCore.getConnection()
                                .update(QUESTINFO)
                                .set(QUESTINFO.VALUE, count)
                                .where(QUESTINFO.KEY.eq(mob))
                                .execute();
                    });
                });
    }
}
