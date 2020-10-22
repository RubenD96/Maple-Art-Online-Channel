package field

import field.obj.Foothold
import field.obj.life.FieldLifeSpawnPoint
import field.obj.portal.FieldPortal
import field.obj.reactor.FieldReactorSpawnPoint
import java.awt.Rectangle

class FieldTemplate(
        val id: Int,
        val returnMap: Int = 0,
        val forcedReturnMap: Int = 0,
        val fieldLimit: Int = 0,
        val name: String,
        val script: String,
        val mapArea: Rectangle,
        val portals: Map<Byte, FieldPortal>,
        val footholds: Map<Int, Foothold>,
        val areas: Set<Rectangle>,
        val mobSpawnPoints: List<FieldLifeSpawnPoint>,
        val npcSpawnPoints: List<FieldLifeSpawnPoint>,
        val reactorSpawnPoints: List<FieldReactorSpawnPoint>
)