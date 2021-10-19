package client.player.quest.reward

import scripting.dialog.DialogUtils.wzImage

class MesoQuestReward(override val value: Int) : QuestReward {

    override val type: QuestRewardType = QuestRewardType.MESOS

    override val message: String = "${"UI/UIWindow.img/QuestIcon/7/0".wzImage()} $value col"
}