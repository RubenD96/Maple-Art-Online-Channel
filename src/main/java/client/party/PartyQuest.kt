package client.party

import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import kotlinx.coroutines.*
import net.maple.packets.CharacterPackets.message
import net.server.Server

class PartyQuest(val type: PartyQuestType, val party: Party) {

    val startTime = System.currentTimeMillis()
    var timer: Job? = null
    var endTime = System.currentTimeMillis()
        set(value) {
            timer?.cancel()

            field += value

            timer = GlobalScope.launch(Dispatchers.Default) {
                delay(field - startTime)
                fail()
            }
        }

    private val participants = HashSet<Int>()

    fun fail() {
        timer?.cancel()

        synchronized(participants) {
            participants.forEach {
                Server.getCharacter(it)?.let { chr ->
                    chr.changeField(type.startMap)
                    chr.message(NoticeWithoutPrefixMessage("[${type.name}] You failed to complete the party quest!"))
                }
            }

            participants.clear()
        }

        party.partyQuest = null
    }

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

    fun finish() {
        timer?.cancel()
        val finalTime = System.currentTimeMillis() - startTime

        synchronized(participants) {
            participants.forEach {
                Server.getCharacter(it)?.message(NoticeWithoutPrefixMessage("[${type.name}] final time $finalTime"))
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