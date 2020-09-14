package client.player.quest

import client.player.quest.requirement.EndingRequirement
import client.player.quest.requirement.StartingRequirement

class QuestTemplate(val id: Int) {
    val startingRequirements = StartingRequirement()
    val endingRequirements = EndingRequirement()
}