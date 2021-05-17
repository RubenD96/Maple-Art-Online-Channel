package constants

object GameConstants {

    private val closeness = intArrayOf(
        1, 3, 6, 14, 31, 60, 108, 181, 287, 434,
        632, 891, 1224, 1642, 2161, 2793, 3557, 4467, 5542,
        6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074, 30000
    )

    fun getPetLevel(tameness: Short): Byte {
        repeat(closeness.size) {
            if (closeness[it] >= tameness) {
                return it.toByte()
            }
        }
        return 30
    }
}