package client.inventory.item.variation

import kotlin.math.ceil

class ItemVariation(private val type: ItemVariationType) {

    operator fun get(base: Int): Int {
        if (base <= 0 || type == ItemVariationType.BASE) return base

        var new = base
        val rand = ceil(Math.random() * 100).toInt()
        when(type) {
            ItemVariationType.BROKEN -> {
                new -= when {
                    rand <= 50 -> 5
                    rand <= 75 -> randomFrom(2, 3, 4)
                    else -> 1
                }
            }
            ItemVariationType.BAD -> {
                new -= when {
                    rand <= 10 -> randomFrom(4, 5)
                    rand <= 60 -> randomFrom(1, 2, 3)
                    else -> 0
                }
            }
            ItemVariationType.NORMAL -> {
                new += when {
                    rand <= 25 -> randomFrom(-1, -2)
                    rand <= 50 -> randomFrom(1, 2)
                    else -> 0
                }
            }
            ItemVariationType.GOOD -> {

            }
            ItemVariationType.GREAT -> {

            }
            ItemVariationType.LEGENDARY -> {

            }
            ItemVariationType.GODLIKE -> {

            }
            else -> {

            }
        }

        return if (new > 0) new else 0
    }

    companion object {
        private fun randomFrom(vararg ints: Int): Int {
            if (ints.isEmpty()) return 0
            return ints[(Math.random() * ints.size).toInt()]
        }
    }
}