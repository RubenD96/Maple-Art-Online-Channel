package client.mastery.type

import client.Character
import client.mastery.AbstractMastery
import client.mastery.MasteryType

class Divination(override val chr: Character, override var level: Int, override var exp: Int) : AbstractMastery() {

    override val type = MasteryType.AGILITY

    override fun levelUp() {
        TODO("Not yet implemented")
    }
}