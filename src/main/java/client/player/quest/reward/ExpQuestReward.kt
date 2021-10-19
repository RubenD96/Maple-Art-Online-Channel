package client.player.quest.reward

import scripting.dialog.DialogUtils.wzImage

class ExpQuestReward(override val value: Int) : QuestReward {

    override val type: QuestRewardType = QuestRewardType.EXP

    override val message: String = "${"UI/UIWindow.img/QuestIcon/8/0".wzImage()} $value exp"
}