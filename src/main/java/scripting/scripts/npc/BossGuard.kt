package scripting.scripts.npc

import client.Client
import client.party.PartyMember
import field.obj.life.FieldMob
import net.server.Server
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.green
import scripting.dialog.DialogUtils.mobName
import scripting.dialog.DialogUtils.red
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc(
    [
        9000027, // Kyrin, floor 1
    ]
)
class BossGuard : NPCScript() {

    private data class FloorInfo(
        val preMessage: String, // first message before you enter
        val boss: Int, // boss mob id
        val exitMap: Int, // map to tp to after the boss is defeated
        val fightMessage: String, // during fight, boss not defeated yet
        val endMessage: String, // boss defeated
        val warpMessage: String, // message after warping out
        val nextTownMessage: String = "", // talking to the npc again on the new floor (not always present)
        val prepareMessage: String = "Be sure to prepare properly!" // clicking end chat before starting
        //val returnMap: Int = -1 // map to tp to if you quit the bossfight, defaulted to floor + 999 (1999 for floor 1)
    )

    private val DialogContext.chr get() = c.character
    private val DialogContext.mapMembers get() = chr.party!!.getMembersOnSameField(chr.field)
    private val DialogContext.info: FloorInfo
        get() {
            val sub = chr.fieldId % 1000
            if (sub == 998 || sub == 999) {
                return floors[chr.fieldId / 1000]!!
            }
            return floors[(chr.fieldId / 1000) - 1]!!
        }

    private companion object {
        val floors: Map<Int, FloorInfo> = mapOf(
            1 to FloorInfo(
                "Sup before start",
                9700050,
                2110,
                "Hehe u gonna die",
                "Neat, I didn't think you had it in you!",
                "Next town is just up ahead",
                "Boyyyyyyyyyy u done did it"
            )
        )
    }

    override fun execute(c: Client) {
        start(c) {
            with(it) {
                if (chr.fieldId % 1000 == 998) beforeFight()
                else if (chr.fieldId % 1000 == 999) duringFight()
                else afterFight()
            }
        }
    }

    private fun DialogContext.beforeFight() {
        sendMessage(
            info.preMessage,
            next = {
                chr.party?.let {
                    if (it.onlineMembers.size == 1) return@let // single player party (no members)
                    if (it.leaderId == chr.id) {
                        var message = "Are you sure you want to fight the boss with your ${"party".red().bold()}?"
                        if (mapMembers.size != it.onlineMembers.size) {
                            message += "\r\n\r\nNot all party members are in the map, only the party members that are present on this map will participate."
                                .bold().red()
                        }
                        sendMessage(
                            message,
                            yes = { warpStart(mapMembers) }
                        )
                    } else {
                        endMessage("You are not the party leader. If you want to try the boss solo please leave your current party first.")
                    }
                    return@sendMessage
                }

                sendMessage(
                    "Are you sure you want to fight the boss ${"alone".red().bold()}?",
                    yes = { warpStart() }
                )
            }
        )
    }

    private fun DialogContext.warpStart(members: List<PartyMember>? = null) {
        members?.let {
            if (mayPartyEnter(it)) {
                // pre-load the field once, so everybody goes to the same field, and not 6 different instances
                val field = chr.getChannel().fieldManager.getField(chr.fieldId + 1) // 998 + 1 = 999 :)
                it.forEach { member ->
                    Server.getCharacter(member.cid)?.changeField(field)
                }
            } else {
                sendMessage(
                    "1 or more members in the party have already defeated this boss once while 1 or more members haven't." +
                            "\r\nMake sure your party is either full of people that defeated the boss before or full of people that haven't.",
                    ok = { clearStates() }, // no prepareMessage
                    end = { clearStates() }
                )
            }
        } ?: run { // solo
            chr.changeField(chr.getChannel().fieldManager.getField(chr.fieldId + 1))
        }
    }

    private fun DialogContext.mayPartyEnter(members: List<PartyMember>): Boolean {
        var killedCounter = 0
        members.forEach {
            Server.getCharacter(it.cid)!!.mobKills[info.boss]?.let { killedCounter++ }
        }

        if (killedCounter > 0) return killedCounter == members.size
        return true
    }

    private fun DialogContext.duringFight() {
        chr.field.getObjects<FieldMob>().firstOrNull { it.template.id == info.boss }?.let {
            endMessage(info.fightMessage)
        } ?: run {
            var message = "${info.endMessage}\r\n\r\nDamage done to ${info.boss.mobName().red()}\r\n"
            chr.field.bossDamage.forEach { (name, damage) ->
                message += "${name.blue()}: ${damage.green()}\r\n".bold()
            }
            message += "\r\nDo you wish to go to the next floor right now?"

            sendMessage(
                message,
                yes = {
                    chr.changeField(info.exitMap)
                    endMessage(info.warpMessage)
                },
                no = {
                    endMessage("Let me know when you're ready!")
                }
            )
        }
    }

    private fun DialogContext.afterFight() {
        endMessage(info.nextTownMessage)
    }

    override fun DialogContext.onEnd() {
        if (chr.fieldId % 1000 == 998) endMessage(info.prepareMessage)
        else clearStates()
    }
}