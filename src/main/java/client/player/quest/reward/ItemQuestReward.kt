package client.player.quest.reward

import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.itemDetails
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName

class ItemQuestReward(override val value: Int, val quantity: Short) : QuestReward {

    override val type: QuestRewardType = QuestRewardType.ITEM

    override val message: String = "${value.itemImage()} $quantity ${value.itemDetails().blue()}"
}