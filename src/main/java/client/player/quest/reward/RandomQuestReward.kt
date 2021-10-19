package client.player.quest.reward

import scripting.dialog.DialogUtils.wzImage

// todo?
class RandomQuestReward(override val value: Int) : QuestReward {

    override val type: QuestRewardType = QuestRewardType.RANDOM

    override val message: String = "UI/UIWindow.img/QuestIcon/5/0".wzImage()
}