package managers

import field.obj.reactor.ReactorTemplate

object ReactorManager : AbstractManager() {

    private const val fallback = 1

    // TODO assertion test to check if the fallback reactor (TODO, TODO) exists
    init {
        getData("wz/Reactor/${fallback}.mao")!!
    }

    private val reactors: MutableMap<Int, ReactorTemplate> = HashMap()

    fun getReactor(id: Int): ReactorTemplate {
        synchronized(reactors) {
            return reactors[id] ?: run {
                val reactor = ReactorTemplate(id)
                if (!loadReactorData(reactor)) {
                    System.err.println("Reactor $id does not exist!")
                    return getReactor(fallback)
                }
                reactors[id] = reactor
                reactor
            }
        }
    }

    private fun loadReactorData(reactor: ReactorTemplate): Boolean {
        val r = getData("wz/Reactor/" + reactor.id + ".mao") ?: return false

        r.readInteger()
        reactor.name = r.readMapleString()
        reactor.maxState = 1
        return true
    }
}