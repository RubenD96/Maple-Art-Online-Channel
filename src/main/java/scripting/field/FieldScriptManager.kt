package scripting.field

import client.Client
import field.Field
import scripting.AbstractScriptManager

object FieldScriptManager : AbstractScriptManager() {

    fun execute(c: Client, field: Field, script: String) {
        try {
            val map = FieldScriptMethods(c, field)
            val iv = getInvocable("map/$script.js", c)
            if (iv == null) {
                println("Mapscript " + script + " is uncoded. (" + field.template.id + ")")
                return
            }
            engine?.let {
                it.put("field", map)
                iv.invokeFunction("execute")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}