package client.player.quest;

import client.Character;
import client.messages.quest.AbstractQuestMessage;
import client.messages.quest.AbstractQuestRecordMessage;
import client.player.quest.requirement.EndingRequirement;
import client.player.quest.requirement.Requirement;
import client.player.quest.requirement.StartingRequirement;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import managers.QuestTemplateManager;
import net.maple.packets.CharacterPackets;
import scripting.quest.QuestScriptManager;

import java.util.Map;

@RequiredArgsConstructor
public class Quest {

    private final @NonNull int id;
    private @Getter QuestState state;
    private final @NonNull Character character;

    private boolean reqCheck(Requirement reqs) {
        if (character.getLevel() < reqs.getMinLevel()) {
            return false;
        }

        if (reqs.getNpc() != 0 && QuestScriptManager.getInstance().getQms().get(character.getClient()).getNpcId() != reqs.getNpc()) {
            return false;
        }

        if (!reqs.getItems().isEmpty()) { // unnecessary?
            for (Map.Entry<Integer, Short> item : reqs.getItems().entrySet()) {
                if (character.getItemQuantity(item.getKey()) < item.getValue()) {
                    return false;
                }
            }
        }

        // todo quest check
        return true;
    }

    public boolean canStart() {
        QuestTemplate template = QuestTemplateManager.getInstance().getQuest(id);
        StartingRequirement reqs = template.getStartingRequirements();

        if (!reqCheck(reqs)) {
            return false;
        }

        if (!reqs.getJobs().isEmpty()) {
            boolean isJob = false;
            for (int job : reqs.getJobs()) {
                if (job == character.getJob().getValue()) {
                    isJob = true;
                    break;
                }
            }
            if (!isJob) {
                return false;
            }
        }

        if (reqs.getMaxLevel() != 0 && character.getLevel() > reqs.getMaxLevel()) {
            return false;
        }

        // todo date check
        return true;
    }

    public boolean canFinish() {
        QuestTemplate template = QuestTemplateManager.getInstance().getQuest(id);
        EndingRequirement reqs = template.getEndingRequirements();

        if (!reqCheck(reqs)) {
            return false;
        }

        // todo mobs check

        return true;
    }

    public void updateState(AbstractQuestRecordMessage message) {
        state = message.getState();
        character.write(CharacterPackets.message(message));

        // todo save to db
    }
}
