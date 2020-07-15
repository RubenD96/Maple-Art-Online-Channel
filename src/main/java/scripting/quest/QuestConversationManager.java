package scripting.quest;

import client.Client;
import lombok.Getter;
import lombok.NonNull;
import scripting.npc.NPCConversationManager;

@SuppressWarnings("unused")
public class QuestConversationManager extends NPCConversationManager {

    private @Getter final int id;
    private @Getter final boolean start;

    public QuestConversationManager(@NonNull Client c, int npcId, int questId, boolean start) {
        super(c, npcId);
        this.start = start;
        this.id = questId;
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
}
