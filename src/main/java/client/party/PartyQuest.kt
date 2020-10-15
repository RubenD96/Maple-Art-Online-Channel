package client.party

import net.server.Server

class PartyQuest(val type: PartyQuestType, val party: Party) {

    val startTime = System.currentTimeMillis()
    var endTime = System.currentTimeMillis()
    var duration = 0

    private val participants = HashSet<Int>()

    fun warp(field: Int) {
        var leader = false

        synchronized(participants) {
            if (!participants.contains(party.leaderId)) {
                Server.getCharacter(party.leaderId)?.let {
                    it.changeField(field)
                    leader = true
                }
            }

            participants.forEach {
                if (it != party.leaderId || !leader) {
                    Server.getCharacter(it)?.changeField(field)
                }
            }
        }
    }

    fun addParticipant(id: Int) {
        synchronized(participants) {
            participants.add(id)
        }
    }

    fun removeParticipant(id: Int) {
        synchronized(participants) {
            participants.remove(id)
        }
    }

    fun isParticipant(id: Int): Boolean {
        synchronized(participants) {
            return participants.contains(id)
        }
    }
}