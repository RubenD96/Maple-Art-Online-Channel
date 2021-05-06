package scripting.scripts.field

import client.Client
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import net.maple.packets.CharacterPackets.message
import scripting.field.Field
import scripting.field.FieldScript

@Field(["FirstVisit"])
class FirstVisit : FieldScript() {

    override fun execute(c: Client) {
        val chr = c.character
        if (!chr.isTownUnlocked(chr.fieldId)) {
            chr.addTown(chr.fieldId)
            chr.message(NoticeWithoutPrefixMessage("The portal master has updated your progress!"))
        }
    }
}