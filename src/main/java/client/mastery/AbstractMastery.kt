package client.mastery

import client.Character
import constants.UserConstants

abstract class AbstractMastery : MasteryInterface {

    abstract val chr: Character
    abstract var level: Int
    abstract var exp: Int

    override fun gainExp(exp: Int) {
        if (level >= type.maxLevel) return
        if (level >= UserConstants.masteryTable.size) return

        this.exp += exp
        val needed = UserConstants.masteryTable[level]

        if (this.exp >= needed) {
            this.exp -= UserConstants.masteryTable[level] // leftover
            levelUp()
            if (exp > 0) {
                gainExp(this.exp)
            }
        }
    }

    override fun levelUp() {
        //chr.skill[type.skill].level++
    }
}