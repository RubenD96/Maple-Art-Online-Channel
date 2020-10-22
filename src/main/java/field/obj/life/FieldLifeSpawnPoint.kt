package field.obj.life

import java.awt.Point

data class FieldLifeSpawnPoint(
        val id: Int,
        val position: Point,
        val rx0: Int,
        val rx1: Int,
        val cy: Int,
        val time: Int,
        val fh: Short,
        val f: Boolean,
        val hide: Boolean
)