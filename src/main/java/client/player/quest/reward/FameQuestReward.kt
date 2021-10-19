package client.player.quest.reward

import scripting.dialog.DialogUtils.wzImage

class FameQuestReward(override val value: Int) : QuestReward {

    override val type: QuestRewardType = QuestRewardType.FAME

    override val message: String = "${"UI/UIWindow.img/QuestIcon/6/0".wzImage()} $value fame"
}