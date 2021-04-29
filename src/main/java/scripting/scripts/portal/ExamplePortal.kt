package scripting.scripts.portal

import client.Client
import field.obj.portal.FieldPortal
import scripting.portal.Portal
import scripting.portal.PortalScript

@Portal(["ExamplePortal"])
class ExamplePortal : PortalScript() {

    override fun start(c: Client, portal: FieldPortal) {
        portal.enter(c.character)
    }
}