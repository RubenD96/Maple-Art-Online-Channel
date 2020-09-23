package client.player

import util.packet.IntegerValue

enum class Job(override val value: Int) : IntegerValue {

    BEGINNER(0),
    WARRIOR(100),
    MAGE(200);

    val id = value

    companion object {
        fun getById(id: Int): Job {
            return values().firstOrNull { it.id == id } ?: run {
                System.err.println("Job $id does not exist, defaulting to BEGINNER(0)")
                BEGINNER
            }
        }
    }
}