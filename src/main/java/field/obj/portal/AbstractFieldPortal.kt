package field.obj.portal

import util.packet.PacketReader
import java.awt.Point

abstract class AbstractFieldPortal {

    var id = 0
        private set
    var targetMap = 0
        private set
    var type = 0
        private set
    lateinit var name: String
        private set
    var script = ""
        private set
    lateinit var targetName: String
        private set
    lateinit var position: Point
        private set

    fun generate(r: PacketReader) {
        name = r.readMapleString()
        targetName = r.readMapleString()
        val hasScript = r.readBool()
        if (hasScript) {
            script = r.readMapleString()
        }
        position = r.readPoint()
        id = r.readInteger()
        targetMap = r.readInteger()
        type = r.readInteger()
    }

    override fun toString(): String {
        return "AbstractFieldPortal(id=$id, targetMap=$targetMap, type=$type, name=$name, script='$script', targetName=$targetName, position=$position)"
    }
}