package client.player

import util.logging.LogType
import util.logging.Logger.log
import util.packet.IntegerValue

enum class Job(override val value: Int) : IntegerValue {

    WEAPONLESS(0),
    WARRIOR(100),
    DARK_KNIGHT(132),
    MAGE(200);

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