package client.player

import util.logging.LogType
import util.logging.Logger.log
import util.packet.IntegerValue

enum class Job(override val value: Int) : IntegerValue {

    WEAPONLESS(0),
    WARRIOR(100),
    TEST1(101),
    TEST2(102),
    TEST3(103),
    TEST4(104),
    TEST5(105),
    HERO(112),
    DARK_KNIGHT(132),
    MAGE(200),
    TEST6(700),
    TEST7(710),
    TEST8(720);

    val id = value

    companion object {
        fun getById(id: Int): Job {
            return values().firstOrNull { it.id == id } ?: run {
                log(LogType.NULL, "Job $id does not exist, defaulting to WEAPONLESS(0)", this)
                WEAPONLESS
            }
        }
    }
}