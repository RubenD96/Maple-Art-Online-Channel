package field.obj

import util.packet.PacketReader

class Foothold {
    var id = 0
        private set
    var next = 0
        private set
    var prev = 0
        private set
    var x1 = 0
        private set
    var x2 = 0
        private set
    var y1 = 0
        private set
    var y2 = 0
        private set

    fun decode(r: PacketReader) {
        id = r.readInteger()
        x1 = r.readShort().toInt()
        x2 = r.readShort().toInt()
        y1 = r.readShort().toInt()
        y2 = r.readShort().toInt()
        next = r.readInteger()
        prev = r.readInteger()
    }
}