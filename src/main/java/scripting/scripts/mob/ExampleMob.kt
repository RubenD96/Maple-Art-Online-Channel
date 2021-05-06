package scripting.scripts.mob

import client.Character
import client.messages.broadcast.types.NoticeWithoutPrefixMessage
import field.obj.life.FieldMob
import net.maple.packets.CharacterPackets.message
import scripting.mob.Mob
import scripting.mob.MobScript

@Mob([100100])
class ExampleMob : MobScript() {

    override fun onHit(chr: Character, mob: FieldMob, damage: Int) {
        mob.drop(chr)
        chr.message(NoticeWithoutPrefixMessage("Wow good job you did $damage to ${mob.template.name}!!!"))
    }

    override fun onDeath(chr: Character, mob: FieldMob, damage: Int) {
        chr.message(NoticeWithoutPrefixMessage("Wow you killed the poor ${mob.template.name} :("))
    }

    override fun onHeal(mob: FieldMob, amount: Int) {
        mob.field.getObjects<Character>().forEach {
            it.message(NoticeWithoutPrefixMessage("[${mob.template.name}] Haha!! I healed $amount, try to kill me now!"))
        }
    }
}