package client.party

enum class PartyOperationType(val value: Int) {

    PARTYREQ_LOADPARTY(0x00),
    PARTYREQ_CREATENEWPARTY(0x01),
    PARTYREQ_WITHDRAWPARTY(0x02),
    PARTYREQ_JOINPARTY(0x03),
    PARTYREQ_INVITEPARTY(0x04),
    PARTYREQ_KICKPARTY(0x05),
    PARTYREQ_CHANGEPARTYBOSS(0x06),

    PARTYRES_LOADPARTY_DONE(0x07),
    PARTYRES_CREATENEWPARTY_DONE(0x08), // You have created a new party.
    PARTYRES_CREATENEWPARTY_ALREAYJOINED(0x09), // Already have joined a party.
    PARTYRES_CREATENEWPARTY_BEGINNER(0x0A), // A beginner can't create a party.
    PARTYRES_CREATENEWPARTY_UNKNOWN(0x0B), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_WITHDRAWPARTY_DONE(0x0C), // You have quit as the leader of the party. The party has been disbanded. ||
    PARTYRES_WITHDRAWPARTY_NOTJOINED(0x0D), // You have yet to join a party.
    PARTYRES_WITHDRAWPARTY_UNKNOWN(0x0E), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_JOINPARTY_DONE(0x0F), // You have joined the party.
    PARTYRES_JOINPARTY_DONE2(0x10), // 16 You have joined the party.
    PARTYRES_JOINPARTY_ALREADYJOINED(0x11), // Already have joined a party.
    PARTYRES_JOINPARTY_ALREADYFULL(0x12), // The party you're trying to join is already in full capacity.
    PARTYRES_JOINPARTY_OVERDESIREDSIZE(0x13), // nothing, instant return
    PARTYRES_JOINPARTY_UNKNOWNUSER(0x14), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_JOINPARTY_UNKNOWN(0x15), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_INVITEPARTY_SENT(0x16), // [POPUP] You have invited '%s' to your party.
    PARTYRES_INVITEPARTY_BLOCKEDUSER(0x17), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_INVITEPARTY_ALREADYINVITED(0x18), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_INVITEPARTY_ALREADYINVITEDBYINVITER(0x19), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_INVITEPARTY_REJECTED(0x1A), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_INVITEPARTY_ACCEPTED(0x1B), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_KICKPARTY_DONE(0x1C), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_KICKPARTY_FIELDLIMIT(0x1D), // Cannot kick another user in this map
    PARTYRES_KICKPARTY_UNKNOWN(0x1E), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_CHANGEPARTYBOSS_DONE(0x1F), // %s has become the leader of the party. || Due to the party leader disconnecting from the game, %s has been assigned as the new leader.
    PARTYRES_CHANGEPARTYBOSS_NOTSAMEFIELD(0x20), // This can only be given to a party member within the vicinity.
    PARTYRES_CHANGEPARTYBOSS_NOMEMBERINSAMEFIELD(0x21), // Unable to hand over the leadership post; No party member is currently within the vicinity of the party leader.
    PARTYRES_CHANGEPARTYBOSS_NOTSAMECHANNEL(0x22), // You may only change with the party member that's on the same channel.
    PARTYRES_CHANGEPARTYBOSS_UNKNOWN(0x23), // Your request for a party didn't work due to an unexpected error.
    PARTYRES_ADMINCANNOTCREATE(0x24), // As a GM, you're forbidden from creating a party.
    PARTYRES_ADMINCANNOTINVITE(0x25), // As a GM, you're forbidden from joining a party.
    PARTYRES_USERMIGRATION(0x26), //
    PARTYRES_CHANGELEVELORJOB(0x27),
    PARTYRES_SUCCESSTOSELECTPQREWARD(0x28),
    PARTYRES_FAILTOSELECTPQREWARD(0x29),
    PARTYRES_RECEIVEPQREWARD(0x2A),
    PARTYRES_FAILTOREQUESTPQREWARD(0x2B),
    PARTYRES_CANNOTINTHISFIELD(0x2C), // Cannot be done in the current map.
    PARTYRES_SERVERMSG(0x2D),

    PARTYINFO_TOWNPORTALCHANGED(0x2E),
    PARTYINFO_OPENGATE(0x2F),

    EXPEDITIONREQ_LOAD(0x30),
    EXPEDITIONREQ_CREATENEW(0x31),
    EXPEDITIONREQ_INVITE(0x32),
    EXPEDITIONREQ_RESPONSEINVITE(0x33),
    EXPEDITIONREQ_WITHDRAW(0x34),
    EXPEDITIONREQ_KICK(0x35),
    EXPEDITIONREQ_CHANGEMASTER(0x36),
    EXPEDITIONREQ_CHANGEPARTYBOSS(0x37),
    EXPEDITIONREQ_RELOCATEMEMBER(0x38),

    EXPEDITIONNOTI_LOAD_DONE(0x39),
    EXPEDITIONNOTI_LOAD_FAIL(0x3A),
    EXPEDITIONNOTI_CREATENEW_DONE(0x3B),
    EXPEDITIONNOTI_JOIN_DONE(0x3C),
    EXPEDITIONNOTI_YOU_JOINED(0x3D),
    EXPEDITIONNOTI_YOU_JOINED2(0x3E),
    EXPEDITIONNOTI_JOIN_FAIL(0x3F),
    EXPEDITIONNOTI_WITHDRAW_DONE(0x40),
    EXPEDITIONNOTI_YOU_WITHDREW(0x41),
    EXPEDITIONNOTI_KICK_DONE(0x42),
    EXPEDITIONNOTI_YOU_KICKED(0x43),
    EXPEDITIONNOTI_REMOVED(0x44),
    EXPEDITIONNOTI_MASTERCHANGED(0x45),
    EXPEDITIONNOTI_MODIFIED(0x46),
    EXPEDITIONNOTI_MODIFIED2(0x47),
    EXPEDITIONNOTI_INVITE(0x48),
    EXPEDITIONNOTI_RESPONSEINVITE(0x49),

    ADVERNOTI_LOADDONE(0x4A),
    ADVERNOTI_CHANGE(0x4B),
    ADVERNOTI_REMOVE(0x4C),
    ADVERNOTI_GETALL(0x4D),
    ADVERNOTI_APPLY(0x4E),
    ADVERNOTI_RESULTAPPLY(0x4F),
    ADVERNOTI_ADDFAIL(0x50),

    ADVERREQ_ADD(0x51),
    ADVERREQ_REMOVE(0x52),
    ADVERREQ_GETALL(0x53),
    ADVERREQ_REMOVEUSERFROMNOTILIST(0x54),
    ADVERREQ_APPLY(0x55),
    ADVERREQ_RESULTAPPLY(0x56);
}