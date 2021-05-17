package field.obj

import client.Character

interface FieldOwnedObject : FieldObject {

    val owner: Character
}