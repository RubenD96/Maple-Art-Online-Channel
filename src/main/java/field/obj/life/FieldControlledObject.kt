package field.obj.life

import client.Character
import field.obj.FieldObject

interface FieldControlledObject : FieldObject {
    var controller: Character?
}