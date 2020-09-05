package client.player.quest;

import client.player.quest.requirement.EndingRequirement;
import client.player.quest.requirement.StartingRequirement;

public class QuestTemplate {

    private final int id;
    private final StartingRequirement startingRequirements = new StartingRequirement();
    private final EndingRequirement endingRequirements = new EndingRequirement();

    public QuestTemplate(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public StartingRequirement getStartingRequirements() {
        return startingRequirements;
    }

    public EndingRequirement getEndingRequirements() {
        return endingRequirements;
    }
}
