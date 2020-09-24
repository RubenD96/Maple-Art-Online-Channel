package client.mastery

interface MasteryInterface {
    val type: MasteryType
    fun gainExp(exp: Int)
    fun levelUp()
}