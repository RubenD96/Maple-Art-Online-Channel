package client.player.quest.reward

import client.mastery.MasteryType
import scripting.dialog.DialogUtils.wzImage

class MasteryQuestReward(val mastery: MasteryType, override val value: Int) : QuestReward {

    override val type: QuestRewardType = QuestRewardType.MASTERY

    override val message: String = "${"UI/UIWindow.img/QuestIcon/10/0".wzImage()} $value ${mastery.name.toLowerCase()} mastery"
}