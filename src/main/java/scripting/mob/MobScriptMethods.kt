package scripting.mob

import client.Client
import field.obj.life.FieldMob
import scripting.AbstractPlayerInteraction

@Deprecated("Old")
class MobScriptMethods(c: Client, private val mob: FieldMob) : AbstractPlayerInteraction(c) {

    fun getMob(): FieldMob {
        return mob
    }
}