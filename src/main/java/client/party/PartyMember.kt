package client.party

import client.Character

class PartyMember {

    val cid: Int
    var name = ""
    var level = 0
    var channel = -2
    var job = 0
    var field = 0
    var isOnline = false

    constructor(chr: Character) {
        cid = chr.id
        name = chr.name
        level = chr.level
        channel = chr.getChannel().channelId
        job = chr.job.value
        field = chr.fieldId
        isOnline = true
    }

    constructor() {
        cid = 0
    }

    fun loadParty(chr: Character, party: Party) {
        isOnline = true
        field = chr.fieldId
        channel = chr.getChannel().channelId

        party.update()
        chr.updatePartyHP(true)
    }

    override fun toString(): String {
        return "PartyMember(cid=$cid, name='$name', level=$level, channel=$channel, job=$job, field=$field, isOnline=$isOnline)"
    }
}