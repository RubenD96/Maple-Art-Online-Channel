package field.obj.life

import client.Character
import util.packet.Packet

abstract class AbstractFieldControlledLife : AbstractFieldLife(), FieldControlledObject {

    var rx0 = 0
    var rx1 = 0
    var cy = 0
    var hide = false
    var f = false
    lateinit var name: String

    override var controller: Character? = null
        set(controller) {
            if (this.controller == null || this.controller != controller) {
                controller?.let {
                    it.controlledObjects.remove(this)
                    if (it.field === this.field) {
                        it.write(getChangeControllerPacket(false))
                    }
                }

                field = controller?.let {
                    it.controlledObjects.add(this)
                    it.write(getChangeControllerPacket(true))
                    it
                }
            }
        }

    protected abstract fun getChangeControllerPacket(setAsController: Boolean): Packet
}