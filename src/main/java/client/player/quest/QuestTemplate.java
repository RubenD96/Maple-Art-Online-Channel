package client.player.quest;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class QuestTemplate {

    private final @NonNull int id;
    private final Map<QuestRequirementType, Integer> startingRequirements = new EnumMap<>(QuestRequirementType.class);
    private final Map<QuestRequirementType, Integer> endingRequirements = new EnumMap<>(QuestRequirementType.class);
    private final Map<Integer, Integer> mobs = new HashMap<>();
}
