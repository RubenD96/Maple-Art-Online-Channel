package client.player.quest.reward

import scripting.dialog.DialogUtils.wzImage

class ClosenessQuestReward(override val value: Int) : QuestReward {

    override val type: QuestRewardType = QuestRewardType.CLOSENESS

    override val message: String = "${"UI/UIWindow2.img/QuestIcon/6/0".wzImage()} $value fame"
}