package scripting.quest;

import client.Client;
import lombok.Getter;
import scripting.AbstractScriptManager;

import javax.script.Invocable;
import java.util.HashMap;
import java.util.Map;

public class QuestScriptManager extends AbstractScriptManager {

    private final @Getter Map<Client, QuestConversationManager> qms = new HashMap<>();
    private final Map<Client, Invocable> scripts = new HashMap<>();
    private static QuestScriptManager instance;

    public static QuestScriptManager getInstance() {
        if (instance == null) {
            instance = new QuestScriptManager();
        }
        return instance;
    }

    private QuestScriptManager() {

    }

    public void converse(Client c, int npc, int questId, boolean start) {
        converse(c, npc, questId, start, 1, 0);
    }

    public void converse(Client c, int mode, int selection) {
        QuestConversationManager qm = qms.get(c);
        converse(c, qm.getNpcId(), qm.getId(), qm.isStart(), mode, selection);
    }

    public boolean converse(Client c, int npc, int questId, boolean start, int mode, int selection) {
        //MapleQuest quest = MapleQuest.getInstance(questid);
        /*if (!c.getPlayer().getQuest(quest).getStatus().equals(MapleQuestStatus.Status.NOT_STARTED)) {
            dispose(c);
            return;
        }*/
        try {
            if (scripts.get(c) == null) {
                QuestConversationManager qm = new QuestConversationManager(c, npc, questId, start);
                if (qms.containsKey(c)) {
                    dispose(c);
                }
                if (c.canClickNPC()) {
                    qms.put(c, qm);
                    Invocable iv = getInvocable("quest/" + questId + ".js", c);
                    if (iv == null) {
                        System.out.println("Quest " + questId + " is uncoded.");
                        return false;
                    }
                    engine.put("cm", qm);
                    scripts.put(c, iv);
                    c.setLastNpcClick(System.currentTimeMillis());
                    iv.invokeFunction(start ? "start" : "end", mode, selection);
                } else {
                    c.getCharacter().enableActions();
                }
            } else {
                c.setLastNpcClick(System.currentTimeMillis());
                scripts.get(c).invokeFunction(start ? "start" : "end", mode, selection);
            }
        } catch (final Exception e) {
            System.err.println(e.getMessage());
            dispose(c);
            return false;
        }
        return true;
    }

    public void dispose(QuestConversationManager qm, Client c) {
        qms.remove(c);
        scripts.remove(c);
        resetContext("quest/" + qm.getId() + ".js", c);
    }

    public void dispose(Client c) {
        QuestConversationManager qm = qms.get(c);
        if (qm != null) {
            dispose(qm, c);
        }
    }
}
