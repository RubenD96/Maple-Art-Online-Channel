package client.party

class PartyQuest(val type: PartyQuestType, val party: Party) {

    val startTime = System.currentTimeMillis()
    val fallenMembers = HashSet<Int>()
}