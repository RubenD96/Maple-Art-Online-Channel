package client.player.quest;

import client.Character;
import client.messages.quest.AbstractQuestRecordMessage;
import client.messages.quest.PerformQuestRecordMessage;
import client.player.quest.requirement.EndingRequirement;
import client.player.quest.requirement.Requirement;
import client.player.quest.requirement.StartingRequirement;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import managers.QuestTemplateManager;
import net.database.QuestAPI;
import net.maple.SendOpcode;
import net.maple.packets.CharacterPackets;
import scripting.quest.QuestScriptManager;
import util.packet.Packet;
import util.packet.PacketWriter;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Quest {

    private final @Getter @NonNull int id;
    private @Getter @Setter QuestState state;
    private final @Getter @NonNull Character character;
    private final @Getter Map<Integer, String> mobs = new LinkedHashMap<>();

    public void initializeMobs() {
        QuestTemplate template = QuestTemplateManager.getInstance().getQuest(id);
        EndingRequirement reqs = template.getEndingRequirements();

        if (!reqs.getMobs().isEmpty()) {
            reqs.getMobs().keySet().forEach(mob -> {
                character.getRegisteredQuestMobs().add(mob);
                mobs.put(mob, "000");
            });
        }
    }

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

        if (!reqs.getQuests().isEmpty()) {
            for (Map.Entry<Integer, Byte> quest : reqs.getQuests().entrySet()) {
                if (quest.getValue() == 0) { // not started
                    if (character.getQuests().containsKey(quest.getKey())) { // but does exist??
                        if (character.getQuests().get(quest.getKey()).getState() != QuestState.NONE) { // shouldn't happen, I think? Checking anyway.
                            return false;
                        }
                    }
                } else {
                    Quest q = character.getQuests().get(quest.getKey());
                    if (q == null) { // does not exist but quest should be started or finished
                        return false;
                    } else if (q.getState().getValue() != quest.getValue()) { // quest is registered at started but should be completed, or other way around
                        return false;
                    }
                }
            }
        }
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

    public void progress(int mob) {
        int count = Integer.parseInt(mobs.get(mob)) + 1;
        StringBuilder newCount = new StringBuilder(String.valueOf(count));
        while (newCount.length() < 3) {
            newCount.insert(0, "0");
        }

        mobs.put(mob, newCount.toString());

        updateMobs(new PerformQuestRecordMessage((short) id, getProgress()));
    }

    public String getProgress() {
        StringBuilder sb = new StringBuilder();
        mobs.values().forEach(count -> sb.insert(0, count));

        return sb.toString();
    }

    private void updateMobs(PerformQuestRecordMessage message) {
        character.write(CharacterPackets.message(message));
    }

    public void updateState(AbstractQuestRecordMessage message) {
        state = message.getState();
        character.write(CharacterPackets.message(message));

        if (state == QuestState.NONE) {
            QuestAPI.remove(this);
        } else if (state == QuestState.PERFORM) {
            QuestAPI.register(this);
        } else if (state == QuestState.COMPLETE) {
            QuestAPI.update(this);
        }
    }

    public Packet startQuestPacket(int npc) {
        PacketWriter pw = new PacketWriter(5);

        pw.writeHeader(SendOpcode.USER_QUEST_RESULT);
        pw.write(0x0A); // QUESTRES_ACT_SUCCESS
        pw.writeShort(id);
        pw.writeInt(npc);
        pw.writeInt(0); // nextQuest

        return pw.createPacket();
    }
}
