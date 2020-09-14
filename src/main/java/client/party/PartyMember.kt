package client.party

import client.Character

/**
 * TODO: this class is very susceptible for null pointers, think of another solution
 */
class PartyMember {

    val cid: Int
    var name = ""
    var level = 0
    var channel = -2
    var job = 0
    var field = 0
    var isOnline = false
    var character: Character? = null

    constructor(chr: Character) {
        cid = chr.id
        name = chr.getName()
        level = chr.level
        channel = chr.channel.channelId
        job = chr.job.value
        field = chr.fieldId
        isOnline = true
        character = chr
    }

    constructor() {
        cid = 0
    }

    /**
     * TODO: see top of file todo
     */
    fun loadParty(party: Party) {
        isOnline = true
        field = character!!.fieldId
        channel = character!!.channel.channelId

        party.update()
        character!!.updatePartyHP(true)
    }

    override fun toString(): String {
        return "PartyMember(cid=$cid, name='$name', level=$level, channel=$channel, job=$job, field=$field, isOnline=$isOnline)"
    }
}