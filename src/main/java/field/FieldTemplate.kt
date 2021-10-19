package field

import field.obj.life.FieldLifeSpawnPoint
import field.obj.portal.FieldPortal
import field.obj.reactor.FieldReactorSpawnPoint
import moe.maple.miho.space.PhysicalSpace2D
import java.awt.Rectangle

class FieldTemplate(
    val id: Int,
    val returnMap: Int = 0,
    val forcedReturnMap: Int = 0,
    val fieldLimit: Int = 0,
    val mapName: String,
    val streetName: String,
    val script: String,
    val mapArea: Rectangle,
    val portals: Map<Byte, FieldPortal>,
    val footholds: PhysicalSpace2D,
    val areas: Set<Rectangle>,
    val mobSpawnPoints: List<FieldLifeSpawnPoint>,
    val npcSpawnPoints: List<FieldLifeSpawnPoint>,
    val reactorSpawnPoints: List<FieldReactorSpawnPoint>
)