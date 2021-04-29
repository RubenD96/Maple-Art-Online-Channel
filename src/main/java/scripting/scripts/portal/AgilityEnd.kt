package scripting.scripts.portal

import client.Client
import field.obj.portal.FieldPortal
import scripting.portal.Portal
import scripting.portal.PortalScript

@Portal(["AgilityEnd"])
class AgilityEnd : PortalScript() {

    override fun start(c: Client, portal: FieldPortal) {
        c.character.moveCollections[c.character.fieldId]!!.export()
        c.character.field.startReplay()
        portal.enter(c.character)
    }
}