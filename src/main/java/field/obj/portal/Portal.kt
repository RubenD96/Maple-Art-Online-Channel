package field.obj.portal

import client.Character

interface Portal {
    fun enter(chr: Character)
    fun leave(chr: Character)
}