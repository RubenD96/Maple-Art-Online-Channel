package scripting.portal

import client.Client
import field.obj.portal.FieldPortal
import scripting.Script

abstract class PortalScript : Script<String> {

    override var value: String = ""
    val name get() = value

    override fun execute(c: Client) {
        error("wtf r u doin")
    }

    abstract fun onEnter(c: Client, portal: FieldPortal)
}