package managers;

import client.player.quest.QuestTemplate;
import util.packet.PacketReader;

import java.util.HashMap;
import java.util.Map;

public class QuestTemplateManager extends AbstractManager {

    private static final Map<Integer, QuestTemplate> templates = new HashMap<>();

    public static QuestTemplate getQuest(int id) {
        QuestTemplate template = templates.get(id);
        if (template == null) {
            template = new QuestTemplate(id);
            loadQuestData(template);
            templates.put(id, template);
        }
        return template;
    }

    private static void loadQuestData(QuestTemplate template) {
        PacketReader r = getData("wz/Quest/" + template.getId() + ".mao");

        if (r != null) {
            int sflags = r.readInteger();
            int eflags = r.readInteger();

            template.getStartingRequirements().decode(sflags, r);
            template.getEndingRequirements().decode(eflags, r);

            //System.out.println("Finished initializing quest: " + template.getId());
        }
    }
}
