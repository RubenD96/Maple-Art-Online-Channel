package client.player.quest;

import client.player.quest.requirement.EndingRequirement;
import client.player.quest.requirement.StartingRequirement;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuestTemplate {

    private final @NonNull int id;
    private final StartingRequirement startingRequirements = new StartingRequirement();
    private final EndingRequirement endingRequirements = new EndingRequirement();
}
