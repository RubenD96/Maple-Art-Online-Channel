package client.player.quest.reward

interface QuestReward {

    val type: QuestRewardType
    val value: Int
    val message: String
}