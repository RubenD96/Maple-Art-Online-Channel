package client.player

import util.packet.IntegerValue

enum class Job(override val value: Int) : IntegerValue {

    BEGINNER(0),
    WARRIOR(100),
    MAGE(200);

    val id = value

    companion object {
        fun getById(id: Int): Job? {
            for (job in values()) {
                if (job.id == id) {
                    return job
                }
            }
            return null
        }
    }
}