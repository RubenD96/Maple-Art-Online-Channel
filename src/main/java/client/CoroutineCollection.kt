package client

import kotlinx.coroutines.Job

/**
 * Collection class that keeps track of all active coroutines for the player so they can be canceled easily.
 * Coroutines in the collection must be unique due to CoroutineType enum.
 * No two coroutines of the same type can be active at the same time.
 */
class CoroutineCollection {

    private val coroutines = HashMap<CoroutineType, Job>()

    fun register(type: CoroutineType, job: Job) {
        cancel(type)
        synchronized(coroutines) {
            coroutines[type] = job
        }
    }

    fun cancel(type: CoroutineType) {
        synchronized(coroutines) {
            coroutines[type]?.let {
                it.cancel()
                coroutines.remove(type)
            }
        }
    }

    fun cancelAll() {
        synchronized(coroutines) {
            coroutines.values.forEach {
                it.cancel()
            }
            coroutines.clear()
        }
    }

    fun getActiveTypes(): Set<CoroutineType> {
        return coroutines.keys
    }
}