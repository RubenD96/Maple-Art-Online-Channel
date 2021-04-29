package scripting.portal

import client.Client
import field.obj.portal.FieldPortal
import scripting.Script

abstract class PortalScript : Script<String> {

    override var value: String = ""
    val name get() = value

    override fun start(c: Client) {
        PortalScriptManager.portalError(name)
    }

    abstract fun start(c: Client, portal: FieldPortal)
}