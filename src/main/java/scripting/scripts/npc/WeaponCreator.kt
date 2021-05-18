package scripting.scripts.npc

import client.Client
import client.inventory.item.slots.ItemSlotEquip
import net.maple.packets.CharacterPackets.modifyInventory
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.itemDetails
import scripting.dialog.DialogUtils.itemImage
import scripting.dialog.DialogUtils.itemName
import scripting.dialog.DialogUtils.letters
import scripting.dialog.DialogUtils.red
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc

@Npc([9060000])
class WeaponCreator : NPCScript() {

    override fun execute(c: Client) {
        start(c) {
            with(it) {
                if (!c.isAdmin) {
                    onEnd()
                    return@start
                }

                selectId()
            }
        }
    }

    private fun DialogContext.selectId() {
        sendGetNumber(
            "Stat Editor".letters() + "\r\n\r\n" +
                    "Provide a valid equip id:",
            min = 1000000,
            max = 1999999,
            def = 1302000,
            positive = { id ->
                DialogUtils.createEquip(id)?.let {
                    showStats(it)
                } ?: run {
                    sendMessage(
                        "Error".letters() + "\r\n\r\n" +
                                "The id you entered does not exist!\r\n" +
                                "Your input was ${id.red()}\r\n" +
                                "Potential item name: ${id.itemName().red()}",
                        ok = { selectId() }
                    )
                }
            }
        )
    }

    private fun DialogContext.showStats(item: ItemSlotEquip) {
        val selections: LinkedHashMap<String, ((Int) -> Unit)> = linkedMapOf(
            "Create Item\r\n".red().bold() to { createItem(item) },
            "RUC: ${item.ruc.red()}" to { editStatDialog("Upgrades Left", item.ruc, item) { item.ruc = it } },
            "CUC: ${item.cuc.red()}\r\n" to { editStatDialog("Upgrades Used", item.cuc, item) { item.cuc = it } },
            "STR: ${item.str.red()}" to { editStatDialog("Strength", item.str, item) { item.str = it } },
            "DEX: ${item.dex.red()}" to { editStatDialog("dexterity", item.dex, item) { item.dex = it } },
            "LUK: ${item.luk.red()}" to { editStatDialog("Luck", item.luk, item) { item.luk = it } },
            "INT: ${item.int.red()}\r\n" to { editStatDialog("Intelligence", item.int, item) { item.int = it } },
            "HP: ${item.maxHP.red()}" to { editStatDialog("Max Health", item.maxHP, item) { item.maxHP = it } },
            "MP: ${item.maxMP.red()}\r\n" to { editStatDialog("Max Mana", item.maxMP, item) { item.maxMP = it } },
            "PAD: ${item.pad.red()}" to { editStatDialog("Weapon Attack", item.pad, item) { item.pad = it } },
            "MAD: ${item.mad.red()}" to { editStatDialog("Magic Attack", item.mad, item) { item.mad = it } },
            "PDD: ${item.pdd.red()}" to { editStatDialog("Weapon Defense", item.pdd, item) { item.pdd = it } },
            "MDD: ${item.mdd.red()}" to { editStatDialog("Magic Defense", item.mdd, item) { item.mdd = it } },
            "ACC: ${item.acc.red()}" to { editStatDialog("Accuracy", item.acc, item) { item.acc = it } },
            "EVA: ${item.eva.red()}\r\n" to { editStatDialog("Evasion", item.eva, item) { item.eva = it } },
            "CRAFT: ${item.craft.red()}" to { editStatDialog("Craft", item.craft, item) { item.craft = it } },
            "SPEED: ${item.speed.red()}" to { editStatDialog("Speed", item.speed, item) { item.speed = it } },
            "JUMP: ${item.jump.red()}" to { editStatDialog("Jump", item.jump, item) { item.jump = it } },
            "ATT: ${item.attribute.red()}\r\n" to { editStatDialog("Attribute", item.attribute, item) { item.attribute = it } },
            "LVL: ${item.level.red()}" to { editStatDialog("Level", item.level, item) { item.level = it } },
            "EXP: ${item.exp.red()}" to { editStatDialog("Experience", item.exp, item) { item.exp = it } },
            "DUR: ${item.durability.red()}\r\n" to { editStatDialog("Durability", item.durability, item) { item.durability = it } },
            "IUC: ${item.iuc.red()}" to { editStatDialog("Hammers Applied", item.iuc, item) { item.iuc = it } },
            "GRADE: ${item.grade.red()}" to { editStatDialog("Grade", item.grade, item) { item.grade = it } },
            "CHUC: ${item.chuc.red()}\r\n" to { editStatDialog("Stars", item.chuc, item) { item.chuc = it } },
            "OPT1: ${item.option1.red()}" to { editStatDialog("Potential One", item.option1, item) { item.option1 = it } },
            "OPT2: ${item.option2.red()}" to { editStatDialog("Potential Two", item.option2, item) { item.option2 = it } },
            "OPT3: ${item.option3.red()}" to { editStatDialog("Potential Three", item.option3, item) { item.option3 = it } }
        )

        sendSimple(
            "Selected equip: " +
                    "${item.templateId.itemImage()} " +
                    "${item.templateId.itemDetails().red()} " +
                    "(${item.templateId.blue()})",
            selections = selections
        )
    }

    private inline fun <reified T : Number> DialogContext.editStatDialog(stat: String, def: T, item: ItemSlotEquip, crossinline action: ((T) -> Unit)) {
        var max: Number = 0
        when (def) {
            is Byte -> max = Byte.MAX_VALUE
            is Short -> max = Short.MAX_VALUE
            is Int -> max = Int.MAX_VALUE
        }

        sendGetNumber(
            stat.letters() + "\r\n\r\n" +
                    "New value:",
            min = 0,
            max = max.toInt(),
            def = def.toInt(),
            positive = { value ->
                when (def) {
                    is Byte -> action(value.toByte() as T)
                    is Short -> action(value.toShort() as T)
                    is Int -> action(value as T)
                }
                showStats(item)
            }
        )
    }

    private fun DialogContext.createItem(item: ItemSlotEquip) {
        c.character.modifyInventory({it.add(item)})
        sendMessage(
            "Swiggity swooty I'm coming for that booty!",
            ok = { clearStates() },
            end = { clearStates() }
        )
    }

    override fun DialogContext.onEnd() {
        endMessage("Swiggity swooty I'm coming for that booty!".red().bold())
    }
}