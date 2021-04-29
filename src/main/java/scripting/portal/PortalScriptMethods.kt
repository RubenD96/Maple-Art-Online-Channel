package scripting.portal

import client.Client
import field.obj.portal.FieldPortal
import scripting.AbstractPlayerInteraction

@Deprecated("Old")
class PortalScriptMethods(c: Client, private val portal: FieldPortal) : AbstractPlayerInteraction(c) {

    fun getPortal(): FieldPortal {
        return portal
    }

    fun enter() {
        portal.forceEnter(c.character)
    }
}