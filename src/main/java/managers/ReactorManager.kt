package managers

import field.obj.reactor.ReactorEvent
import field.obj.reactor.ReactorSubEvent
import field.obj.reactor.ReactorTemplate

object ReactorManager : AbstractManager() {

    private const val fallback = 2402001

    // assertion test to check if the fallback reactor (2402001, Berry) exists
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

        r.readInteger() // id
        reactor.action = r.readMapleString()

        repeat(r.readShort().toInt()) {
            val event = ReactorEvent()
            repeat(r.readShort().toInt()) {
                event.subEvents.add(ReactorSubEvent(
                        r.readInteger(),
                        r.readInteger()
                ))
            }
            reactor.events.add(event)
        }

        return true
    }
}