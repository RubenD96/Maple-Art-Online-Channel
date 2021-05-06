package scripting.mob

import client.Client
import constants.ServerConstants
import field.obj.life.FieldMob
import scripting.AbstractScriptManager

@Deprecated("Old")
object MobScriptManagerOld : AbstractScriptManager() {

    fun onHit(c: Client, fieldMob: FieldMob) {
        //fieldMob.template.onHit = execute(c, fieldMob, "onHit")
    }

    fun onDeath(c: Client, fieldMob: FieldMob) {
        //fieldMob.template.onDeath = execute(c, fieldMob, "onDeath")
    }

    private fun execute(c: Client, fieldMob: FieldMob, function: String): Boolean {
        try {
            val mob = MobScriptMethods(c, fieldMob)
            val iv = getInvocable("mob/${fieldMob.template.id}.js", c)
            if (iv == null) {
                // todo remove msg
                println("Mobscript " + fieldMob.template.id + " is uncoded. (" + fieldMob.template.id + ")")
                return ServerConstants.DEBUG
            }

            println(function)
            engine?.let {
                it.put("mob", mob)
                iv.invokeFunction(function) // todo add exception, probably
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ServerConstants.DEBUG
        }
        return true
    }
}