package client.player

import util.packet.IntegerValue

enum class Job(val id: Int) : IntegerValue {

    BEGINNER(0),
    WARRIOR(100),
    MAGE(200);

    override fun getValue(): Int {
        return id
    }

    override fun setValue(value: Int) {
        // ...
    }

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