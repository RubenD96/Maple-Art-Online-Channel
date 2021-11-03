package scripting.scripts.npc

import client.Character
import client.Client
import field.obj.FieldObject
import field.obj.drop.AbstractFieldDrop
import field.obj.life.FieldMob
import field.obj.life.FieldNPC
import managers.NPCManager
import net.maple.handlers.user.UserSelectNpcHandler
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.green
import scripting.dialog.DialogUtils.itemDetails
import scripting.dialog.DialogUtils.mobName
import scripting.dialog.DialogUtils.red
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc
import java.lang.StringBuilder

@Npc([1002002])
class Sense : NPCScript() {

    private val DialogContext.field get() = c.character.field
    private val DialogContext.characters get() = field.getObjects<Character>().toList()
    private val DialogContext.mobs get() = field.getObjects<FieldMob>().toList()
    private val DialogContext.npcs get() = field.getObjects<FieldNPC>().toList()
    private val DialogContext.drops get() = field.getObjects<AbstractFieldDrop>().toList()
    private val DialogContext.portals get() = field.portals.values

    override fun execute(c: Client) {
        if (!c.isAdmin) {
            c.script = null
            return
        }

        start(c) {
            it.startMenu()
        }
    }

    private fun DialogContext.startMenu() {
        val selections = LinkedHashMap<String, ((Int) -> Unit)>()
        if (characters.isNotEmpty()) selections["Players".blue()] = { listCharacters() }
        if (mobs.isNotEmpty()) selections["Monsters".blue()] = { listMobs() }
        if (npcs.isNotEmpty()) selections["NPCs".blue()] = { listNpcs() }
        if (drops.isNotEmpty()) selections["Dropped items".blue()] = { listDrops() }
        if (portals.isNotEmpty()) selections["Portals".blue()] = { listPortals() }

        sendSimple(
            "The Universe is under no obligation to make sense to you.",
            selections = selections
        )
    }

    private fun DialogContext.listCharacters() {
        val selections = LinkedHashMap<String, ((Int) -> Unit)>()
        val indexes = ArrayList<Int>()
        characters.forEach { chr ->
            selections["${chr.name.blue()} (ID: ${chr.id.red()} LVL: ${chr.level.red()})"] = {
                showGenericInfo<Character>(it)
            }
            indexes.add(chr.id)
        }

        sendList("There are a total of ${characters.size.blue().bold()} characters on this map.", selections, indexes)
    }

    private fun DialogContext.listMobs() {
        val selections = LinkedHashMap<String, ((Int) -> Unit)>()
        val indexes = ArrayList<Int>()
        mobs.forEach { mob ->
            val template = mob.template
            selections["${template.id.mobName()} - ${template.id} - ${mob.hp} / ${template.maxHP} hp (${mob.id})"] =
                {
                    field.getObject<FieldMob>(it)?.template?.id?.let { id ->
                        c.character.philId = id
                        UserSelectNpcHandler.openNpc(c, NPCManager.getNPC(1002000))
                    } ?: sendMessage(
                        "The mob was probably killed in the meantime...",
                        ok = { listMobs() }
                    )
                }
            indexes.add(mob.id)
        }

        sendList("There are a total of ${mobs.size.blue().bold()} mobs on this map.", selections, indexes)
    }

    private fun DialogContext.listNpcs() {
        val selections = LinkedHashMap<String, ((Int) -> Unit)>()
        val indexes = ArrayList<Int>()
        npcs.forEach { npc ->
            selections["${npc.name} - ${npc.npcId}"] = { showGenericInfo<FieldNPC>(it) }
            indexes.add(npc.id)
        }

        sendList("There are a total of ${npcs.size.blue().bold()} npcs on this map.", selections, indexes)
    }

    private fun DialogContext.listDrops() {
        val selections = LinkedHashMap<String, ((Int) -> Unit)>()
        val indexes = ArrayList<Int>()
        drops.forEach { drop ->
            val str = if (drop.isMeso) {
                "${drop.info} Col".green().bold()
            } else {
                "${drop.info.itemDetails().red()} - ${drop.info.blue()}"
            }

            selections[str] = { showGenericInfo<AbstractFieldDrop>(it) }
            indexes.add(drop.id)
        }

        sendList("There are a total of ${drops.size.blue().bold()} drops on this map.", selections, indexes)
    }

    private fun DialogContext.listPortals() {
        val selections = LinkedHashMap<String, ((Int) -> Unit)>()
        val indexes = ArrayList<Int>()
        portals.forEach { portal ->
            val str = StringBuilder("${portal.name.blue()} - ${"Type: ${portal.type}".red().bold()}")
            if (portal.script != "") {
                str.append("\r\n\t\t\tScript: ${portal.script}")
            }
            if (portal.targetMap != 999999999) {
                str.append("\r\n\t\t\tTm: ${portal.targetMap}")
            }
            if (portal.targetName != "") {
                str.append("\r\n\t\t\tTp: ${portal.targetName}")
            }
            selections[str.toString()] = {
                sendMessage(
                    portal.toString(),
                    ok = { startMenu() }
                )
            }
            indexes.add(portal.id)
        }

        sendList("There are a total of ${portals.size.blue().bold()} portals on this map.", selections, indexes)
    }

    private fun DialogContext.sendList(append: String, selections: LinkedHashMap<String, ((Int) -> Unit)>, indexes: ArrayList<Int>?) {
        sendSimple(
            "Look up at the stars and not down at your feet. Try to make sense of what you see, and wonder about what makes the universe exist. Be curious." +
                    "\r\n\r\n$append",
            selections = selections,
            indexes = indexes,
            end = { startMenu() }
        )
    }

    private inline fun <reified T : FieldObject> DialogContext.showGenericInfo(oid: Int) {
        sendMessage(
            field.getObject<T>(oid).toString(),
            ok = { startMenu() }
        )
    }
}