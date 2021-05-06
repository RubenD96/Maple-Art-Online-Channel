package scripting.mob

import client.Character
import client.Client
import field.obj.life.FieldMob
import scripting.Script

abstract class MobScript : Script<Int> {

    override var value = 0
    val id get() = value

    override fun execute(c: Client) {
        error("wtf r u doin")
    }

    open fun onHit(chr: Character, mob: FieldMob, damage: Int) {}
    open fun onDeath(chr: Character, mob: FieldMob, damage: Int) {}
    open fun onHeal(mob: FieldMob, amount: Int) {}
}