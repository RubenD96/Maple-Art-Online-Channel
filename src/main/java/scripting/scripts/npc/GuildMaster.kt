package scripting.scripts.npc

import client.Client
import net.database.GuildAPI
import net.maple.packets.GuildPackets
import net.maple.packets.GuildPackets.getLoadGuildPacket
import scripting.dialog.DialogContext
import scripting.dialog.DialogUtils.blue
import scripting.dialog.DialogUtils.bold
import scripting.dialog.DialogUtils.green
import scripting.dialog.npc.NPCScript
import scripting.dialog.npc.Npc
import world.guild.Guild

@Npc([2010007])
class GuildMaster : NPCScript() {

    companion object {
        const val createCost = 1500000
        const val incCost = 500000
    }

    private val DialogContext.guild: Guild? get() = c.character.guild

    override fun execute(c: Client) {
        start(c) {
            with(it) {
                start()
            }
        }
    }

    private fun DialogContext.start() {
        val selections = LinkedHashMap<String, ((Int) -> Unit)>()
        selections["Create a Guild".blue()] = { create() }

        guild?.let {
            if (it.leader == c.character.id) {
                selections["Disband your guild".blue()] = { disbandConfirmation() }
                selections["Increase your guild's capacity".blue()] = { increaseConfirmation() }
                selections["Change guild leader".blue()] = {
                    sendMessage(
                        "Not available yet.", // todo
                        ok = { start() }
                    )
                }
            }
        }

        sendSimple(
            "What would you like to do?",
            selections = selections
        )
    }

    private fun DialogContext.create() {
        guild?.let {
            sendMessage(
                "You may not create a new guild while you are in one.",
                ok = { onEnd() }
            )
        } ?: run {
            sendMessage(
                "Creating a guild costs ${"$createCost col".blue()}, are you sure you want to continue?",
                yes = { guildName() },
                no = { onEnd() }
            )
        }
    }

    private fun DialogContext.guildName() {
        if (c.character.meso < createCost) {
            sendMessage(
                "You lack the funds to start a guild!",
                ok = { onEnd() }
            )
        } else {
            sendGetText(
                "Please enter the name of your guild:",
                min = 3,
                max = 45,
                positive = { createGuild(it) }
            )
        }
    }

    private fun DialogContext.createGuild(name: String) {
        if (GuildAPI.guildNames.contains(name)) {
            sendMessage(
                "The name ${name.blue()} is already in use!",
                ok = { guildName() }
            )
        } else {
            c.character.gainMeso(-createCost, true)
            val gid = GuildAPI.create(name, c.character)
            c.character.loadGuildById(gid)
            c.write(c.character.guild!!.getLoadGuildPacket())

            sendMessage(
                "Successfully created ${name.blue().bold()}!",
                ok = { onEnd() }
            )
        }
    }

    private fun DialogContext.disbandConfirmation() {
        sendMessage(
            "Are you sure you want to disband your guild? You will not be able to recover it afterwards.",
            yes = { disband() }
        )
    }

    private fun DialogContext.disband() {
        guild!!.disband()
        onEnd()
    }

    private fun DialogContext.increaseConfirmation() {
        sendMessage(
            "Increasing your guild capacity by ${5.green()} costs ${"$incCost col".blue()}, are you sure you want to continue?",
            yes = { increase() }
        )
    }

    private fun DialogContext.increase() {
        if (c.character.meso < createCost) {
            sendMessage(
                "You lack the funds to increase the capacity for your guild!",
                ok = { onEnd() }
            )
            return
        }

        guild?.let {
            val newSize = it.maxSize + 5
            if (newSize > 255) {
                sendMessage(
                    "Your guild has already reached the max member capacity!",
                    ok = { onEnd() }
                )
            } else {
                with(GuildPackets) {
                    guild!!.increaseMemberSize(newSize.toByte())
                }
            }
        }
    }
}