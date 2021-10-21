package scripting.scripts.npc

import client.Client
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.mapName
import scripting.dialog.DialogUtils.red
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc
import kotlin.math.floor

@Npc([1032102])
class PortalMaster : NPCScript() {

    private val DialogContext.destinations: List<Int>
        get() = c.character.towns.toList().filter { it != c.character.fieldId }

    override fun execute(c: Client) {
        start(c) {
            with(it) {
                val selections = LinkedHashMap<String, ((Int) -> Unit)>()
                destinations.forEach { mid ->
                    selections[getCorrectFloorForTown(mid).blue().bold() + mid.mapName().blue()] =
                        { c.character.changeField(mid, "portal") }
                }

                sendSimple(
                    "Hi I'm ${"Luna".blue()}.\r\n" +
                            "You can use the ${"town portals".red()} to move to other ${"towns".red()}, but you need to have ${"visited".red()} the town before unlocking its portal.\r\n\r\n" +
                            "Where do you want to go?",
                    selections = selections
                )
            }
        }
    }

    private fun getCorrectFloorForTown(town: Int): String {
        return when (town) {
            100 -> ""
            else -> "Floor ${town / 1000}. "
        }
    }
}