package scripting.scripts.npc

import client.Client
import field.obj.drop.DropEntry
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.green
import scripting.dialog.DialogUtils.itemDetails
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName
import scripting.dialog.DialogUtils.letters
import scripting.dialog.DialogUtils.mobName
import scripting.dialog.DialogUtils.purple
import scripting.dialog.DialogUtils.red
import scripting.dialog.SpeakerType
import scripting.dialog.StateHolder
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([1002000])
class DropChanger : NPCScript() {

    private val DialogContext.mobid get() = holder.numberData["mobid"]!! as Int
    private val DialogContext.newItem get() = holder.numberData["newItem"] as? Int
    private val DialogContext.newChance get() = holder.numberData["newChance"] as? Double
    private val DialogContext.mesoMin get() = holder.numberData["mesoMin"] as? Int
    private val DialogContext.mesoMax get() = holder.numberData["mesoMax"] as? Int
    private val DialogContext.mesoChance get() = holder.numberData["mesoChance"] as? Double
    private val DialogContext.mesoUpdate get() = holder.booleanData["update"]!!
    private val DialogContext.item get() = (holder as Data).item
    private val DialogContext.drops get() = (holder as Data).drops

    private val DialogContext.mobStats get() = "${mobid.mobName().blue()} (${mobid.red()})"
    private val DialogContext.currentMobString get() = "Current mob: $mobStats"

    private val DialogContext.itemStats get() = "${item.id.itemImage()} ${item.id.itemName().blue()}"
    private val DialogContext.selectedItemString get() = "You've selected: $itemStats"
    private val DialogContext.currentDropChance get() = "The current drop chance for this item is: ${item.chance.red()}%."

    private val DialogContext.currentItemToAdd: String
        get() {
            val str = "Current item to add:"
            newItem?.let {
                return "$str ${it.itemImage()} ${it.itemName().blue()}"
            } ?: return "$str ${"not set yet!".blue()}"
        }
    private val DialogContext.currentChanceToAdd: String
        get() {
            val str = "Drop chance for item to add:"
            newChance?.let {
                return "$str ${it.red()}"
            } ?: return "$str ${"not set yet!".red()}"
        }

    private class Data : StateHolder() {
        lateinit var drops: List<DropEntry>
        lateinit var item: DropEntry
    }

    override fun execute(c: Client) {
        if (!c.isAdmin) {
            c.script = null
            return
        }

        start(c) {
            it.holder = Data()
            val mobid = c.character.philId
            it.holder.numberData["mobid"] = if (mobid == 0) 100100 else c.character.philId
            it.sendStartMenu()
        }
    }

    private fun DialogContext.sendStartMenu() {
        sendSimple(
            "Phil n Chill".letters() +
                    "\r\n$currentMobString" +
                    "\r\nIf the mobname does not show up it probably means the mob does not exist!",
            selections = linkedMapOf(
                "Change mob id".blue() to { sendChangeMobIdPrompt() },
                "Manage drops".blue() to { sendItemList() },
                "Add new drop".blue() to { sendNewDropPrompt() },
                "Edit meso drop".blue() to {
                    setMesoValues()
                    sendEditMesoPrompt()
                }
            ),
            end = { endMessage("Toodaloo ${"UwU".red().bold()}") }
        )
    }

    private fun DialogContext.sendChangeMobIdPrompt() {
        sendGetNumber(
            "Change Mob".letters() +
                    "\r\n$currentMobString" +
                    "\r\nnew mob id:",
            mobid,
            100100,
            9999999,
            positive = {
                holder.numberData["mobid"] = it
                c.character.philId = it
                sendStartMenu()
            }
        )
    }

    private fun DialogContext.sendItemList() {
        (holder as Data).drops = DialogUtils.getMobDrops(mobid).filter { it.id != 0 }
        val base = "Manage Drops".letters() +
                "\r\nAll items $mobStats drops:"

        if (drops.isEmpty()) {
            sendMessage(
                "$base\r\nThis mob does not drop anything!",
                ok = { sendStartMenu() }
            )
        } else {
            val selections = LinkedHashMap<String, ((Int) -> Unit)>()
            drops.forEach { drop ->
                selections["${drop.id.itemImage()} ${drop.id.itemDetails().blue()} ${drop.chance}%"] = {
                    (holder as Data).item = drops[it]
                    sendEditDropEntry()
                }
            }
            sendSimple(
                text = base,
                selections = selections
            )
        }
    }

    private fun DialogContext.sendEditDropEntry() {
        sendSimple(
            "Edit Item".letters() +
                    "\r\n$selectedItemString" +
                    "\r\n$currentDropChance" +
                    "\r\nWhat would you like to do?",
            selections = linkedMapOf(
                "Change drop-chance on this item".blue() to { sendEditDropChance() },
                "Remove this item from monster's droplist".blue() to { sendDeleteDrop() }
            )
        )
    }

    private fun DialogContext.sendEditDropChance() {
        sendGetText(
            "Change Chance".letters() +
                    "\r\n$selectedItemString" +
                    "\r\n$currentDropChance" +
                    "\r\nNew Drop Chance:",
            def = item.chance.toString(),
            min = 1,
            max = 1000000,
            positive = {
                try {
                    val chance = it.toDouble()
                    if (chance == item.chance) {
                        sendFailed(
                            "No changes were made as the new chance was the same as the old chance."
                        ) { sendEditDropChance() }
                    } else {
                        updateDropChance(chance)
                    }
                } catch (_: NumberFormatException) {
                    sendFailed(
                        "The input $it is not a valid drop chance (NumberFormatException)"
                    ) { sendEditDropChance() }
                }
            }
        )
    }

    private fun DialogContext.sendDeleteDrop() {
        sendMessage(
            "Delete Item".letters() +
                    "\r\n\r\nAre you sure you want to delete $itemStats?",
            yes = { deleteDrop() },
            no = { sendStartMenu() }
        )
    }

    private fun DialogContext.updateDropChance(chance: Double) {
        DialogUtils.editDropChance(mobid, item.id, chance)
        sendMessage(
            "Success".letters() +
                    "\r\n\r\n${item.id.itemImage()} dop chance is changed to " +
                    "${chance.red()}% for ${mobid.mobName().purple()}",
            ok = { sendStartMenu() }
        )
    }

    private fun DialogContext.deleteDrop() {
        DialogUtils.removeDrop(mobid, item.id)
        sendMessage(
            "Deleted".letters() +
                    "\r\n$itemStats has been removed!",
            ok = { sendStartMenu() }
        )
    }

    private fun DialogContext.sendFailed(reason: String, next: (() -> Unit)) {
        sendMessage(
            "Failed".letters() +
                    "\r\n$reason",
            ok = next
        )
    }

    private fun DialogContext.sendNewDropPrompt() {
        sendSimple(
            "Add Item".letters() +
                    "\r\n$currentMobString" +
                    "\r\n$currentItemToAdd" +
                    "\r\n$currentChanceToAdd",
            selections = linkedMapOf(
                "Change itemid".blue() to { sendChangeItemIdPrompt() },
                "Change drop chance".blue() to { sendChangeDropChancePrompt() },
                "Add item".green().bold() to { sendAddItemPrompt() }
            )
        )
    }

    private fun DialogContext.sendChangeItemIdPrompt() {
        sendGetNumber(
            "Change Item".letters() +
                    "\r\n$currentItemToAdd" +
                    "\r\nNew itemID:",
            def = newItem ?: 4000000,
            min = 1000000,
            max = 9999999,
            positive = {
                holder.numberData["newItem"] = it
                sendNewDropPrompt()
            }
        )
    }

    private fun DialogContext.sendChangeDropChancePrompt() {
        sendGetText(
            "Change Chance".letters() +
                    "\r\n$currentItemToAdd" +
                    "\r\n$currentChanceToAdd" +
                    "\r\nNew drop chance:",
            def = newChance?.toString() ?: "100",
            min = 1,
            max = 1000000,
            positive = {
                try {
                    val chance = it.toDouble()
                    holder.numberData["newChance"] = chance
                    sendNewDropPrompt()
                } catch (_: NumberFormatException) {
                    sendFailed(
                        "The input $it is not a valid drop chance (NumberFormatException)"
                    ) { sendNewDropPrompt() }
                }
            }
        )
    }

    private fun DialogContext.sendAddItemPrompt() {
        newItem?.let { item ->
            newChance?.let { chance ->
                DialogUtils.addMobDrop(mobid, item, chance)
                sendMessage(
                    "Success".letters() +
                            "\r\n${item.itemImage()} ${item.itemName().blue()} has been added to " +
                            "${mobid.mobName().purple()} with a drop chance of ${chance.red()}%",
                    ok = { sendStartMenu() }
                )
            } ?: sendFailed("You didn't set a chance!") {
                sendNewDropPrompt()
            }
        } ?: sendFailed("You didn't set an item id!") {
            sendNewDropPrompt()
        }
    }

    private fun DialogContext.setMesoValues() {
        val meso = DialogUtils.getMobDrops(mobid).find { it.id == 0 }
        meso?.let {
            holder.numberData["mesoMin"] = it.min
            holder.numberData["mesoMax"] = it.max
            holder.numberData["mesoChance"] = it.chance
            holder.booleanData["update"] = true
        } ?: run {
            holder.booleanData["update"] = false
        }
    }

    private fun DialogContext.sendEditMesoPrompt() {
        sendSimple(
            "Mesos".letters() +
                    "\r\n$currentMobString" +
                    "\r\nMin meso: ${(mesoMin?.toString() ?: "not set yet").blue()}" +
                    "\r\nMax meso: ${(mesoMax?.toString() ?: "not set yet").blue()}" +
                    "\r\nChance to drop: ${(mesoChance?.toString() ?: "not set yet").red()}",
            selections = linkedMapOf(
                "Change min value".blue() to { sendChangeMesoPrompt(mesoMin, "min") },
                "Change max value".blue() to { sendChangeMesoPrompt(mesoMax, "max") },
                "Change drop chance".blue() to { sendChangeMesoChancePrompt() },
                "Apply changes".green().bold() to { sendApplyMesoChanges() }
            )
        )
    }

    private fun DialogContext.sendChangeMesoPrompt(current: Int?, type: String) {
        sendGetNumber(
            "${type.capitalize()}imum".letters() +
                    "\r\nCurrent $type meso: ${(current?.toString() ?: "not set yet").blue()}" +
                    "\r\nnew $type value:",
            def = current ?: 1,
            min = 1,
            max = Int.MAX_VALUE,
            positive = {
                if (type == "min") holder.numberData["mesoMin"] = it
                else holder.numberData["mesoMax"] = it

                sendEditMesoPrompt()
            }
        )
    }

    private fun DialogContext.sendChangeMesoChancePrompt() {
        sendGetText(
            "Drop Chance".letters() +
                    "\r\nChance to drop: ${(mesoChance?.toString() ?: "not set yet").red()}" +
                    "\r\nNew drop chance:",
            def = mesoChance?.toString() ?: "100",
            min = 1,
            max = 10000,
            positive = {
                try {
                    val chance = it.toDouble()
                    holder.numberData["mesoChance"] = chance
                    sendEditMesoPrompt()
                } catch (_: NumberFormatException) {
                    sendFailed(
                        "The input $it is not a valid drop chance (NumberFormatException)"
                    ) { sendEditMesoPrompt() }
                }
            }
        )
    }

    private fun DialogContext.sendApplyMesoChanges() {
        mesoMin?.let { min ->
            mesoMax?.let { max ->
                if (min > max) {
                    sendFailed("The minimum (${min.blue().bold()}) is higher than the maximum (${max.red().bold()})!") {
                        sendEditMesoPrompt()
                    }
                    return
                }

                mesoChance?.let { chance ->
                    val append: String = if (mesoUpdate) {
                        DialogUtils.editMinMaxChance(mobid, 0, min, max, chance)
                        "changed for"
                    } else {
                        DialogUtils.addMobDrop(mobid, 0, min, max, 0, chance)
                        "added to"
                    }
                    sendMessage(
                        "Success".letters() +
                                "\r\n\r\nMeso drop ($min - $max) has been $append ${mobid.mobName().purple()}" +
                                "With a drop chance of ${chance.red()}%",
                        ok = { sendStartMenu() }
                    )
                } ?: sendFailed("You didn't set a chance!") {
                    sendEditMesoPrompt()
                }
            } ?: sendFailed("You didn't set a max!") {
                sendEditMesoPrompt()
            }
        } ?: sendFailed("You didn't set a min!") {
            sendEditMesoPrompt()
        }
    }

    override fun DialogContext.onEnd() {
        sendSimple(
            "Farewell friend... unless??",
            speaker = SpeakerType.NoESC,
            selections = linkedMapOf(
                "Go back to start".green() to { sendStartMenu() },
                "Close script".red() to { endMessage("Toodaloo ${"UwU".red().bold()}") }
            )
        )
    }
}