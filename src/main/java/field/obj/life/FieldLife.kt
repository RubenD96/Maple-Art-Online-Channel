package field.obj.life

import field.obj.FieldObject

interface FieldLife : FieldObject {
    var moveAction: Byte
    var foothold: Short
}