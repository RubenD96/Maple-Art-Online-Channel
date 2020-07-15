package client.player.quest;

import java.util.HashMap;
import java.util.Map;

public class QuestTemplateManager {

    private final Map<Integer, QuestTemplate> templates;
    private static QuestTemplateManager instance;

    public static QuestTemplateManager getInstance() {
        if (instance == null) {
            instance = new QuestTemplateManager();
        }
        return instance;
    }

    private QuestTemplateManager() {
        templates = new HashMap<>();
    }

    public QuestTemplate getQuest(int id) {
        QuestTemplate template = templates.get(id);
        if (template == null) {
            template = new QuestTemplate(id);
            loadQuestData(template);
            templates.put(id, template);
        }
        return template;
    }

    private void loadQuestData(QuestTemplate template) {
        // todo
    }
}
