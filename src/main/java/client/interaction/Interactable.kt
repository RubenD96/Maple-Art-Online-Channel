package client.interaction

import client.Character
import client.Client

interface Interactable {
    fun open(chr: Character)
    fun close(c: Client)
}