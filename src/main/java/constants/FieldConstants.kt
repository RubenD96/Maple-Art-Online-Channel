package constants

import field.FieldTemplate

object FieldConstants {

    val JQ_FIELDS = intArrayOf(1510, 2010)
    const val HARDCORE_DEATHMAP = 666

    fun FieldTemplate.isSafeMap(): Boolean {
        return id == 200 || // mao event
                id == 900 || // gm map
                id == 14040 ||  // house of green
                isTownMap() ||
                JQ_FIELDS.contains(id) ||
                id in 100..109 ||  // fm
                id in 100000001..199999998 // houses
    }

    fun FieldTemplate.isTownMap(): Boolean {
        return id == 1000 || // town of beginnings
                id == 1500 || // tolbana
                id == 2000 || // urbus
                id == 3000 || // hot sands
                id == 10000 || // skytopia
                id == 11000 || // taft
                id == 12000 || // sunken city
                id == 13000 || // joel's quay
                id == 13300 || // riverdale
                id == 14000 || // magic leaf plaza
                id == 14300 || // sunset valley
                id == 15000 || // snowedin village
                id == 16000 // ...
    }
}