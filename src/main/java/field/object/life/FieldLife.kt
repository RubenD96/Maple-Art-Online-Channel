package field.`object`.life

import field.`object`.FieldObject

interface FieldLife : FieldObject {
    var moveAction: Byte
    var foothold: Short
}