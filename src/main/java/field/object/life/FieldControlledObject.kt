package field.`object`.life

import client.Character
import field.`object`.FieldObject

interface FieldControlledObject : FieldObject {
    var controller: Character?
}