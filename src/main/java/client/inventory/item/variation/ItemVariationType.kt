package client.inventory.item.variation

enum class ItemVariationType(val chance: Double) {

    BASE(0.0),
    BROKEN(10.0),
    BAD(35.0),
    NORMAL(50.0),
    GOOD(4.0),
    GREAT(0.9),
    LEGENDARY(0.1),
    GODLIKE(0.0);

    companion object {
        /**
         * Gets a random variation type
         *
         * @return Any type of variation except BASE or GODLIKE
         */
        fun getRandom(): ItemVariationType {
            val types = listOf(LEGENDARY, GREAT, GOOD, BROKEN, BAD, NORMAL)
            val rand = Math.random() * 100

            var hit = types[0].chance
            for (type in types) {
                if (rand <= hit) {
                    return type
                } else {
                    hit += types[types.indexOf(type) + 1].chance
                }
            }
            return BASE // error
        }
    }
}