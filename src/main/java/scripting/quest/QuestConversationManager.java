package scripting.quest;

import client.Client;
import scripting.npc.NPCConversationManager;

@SuppressWarnings("unused")
public class QuestConversationManager extends NPCConversationManager {

    private final int id;
    private final boolean start;

    public QuestConversationManager(Client c, int npcId, int questId, boolean start) {
        super(c, npcId);
        this.start = start;
        this.id = questId;
    }

    public int getId() {
        return id;
    }

    public boolean isStart() {
        return start;
    }

    @Override
    public void dispose() {
        QuestScriptManager.getInstance().dispose(this, c);
    }

    public void startQuest() {
        super.startQuest(id);
    }

    public void completeQuest() {
        super.completeQuest(id);
    }

    public void reward(int exp, int col, int[][] items) {
        if (exp != 0) gainExp(exp);
        if (col != 0) gainMeso(col);
        if (items.length > 1) {
            massGainItem(items);
        } else if (items.length == 1) {
            gainItem(items[0][0], items[0][1]);
        }
    }
}
