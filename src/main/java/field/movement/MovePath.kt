package field.movement

import field.movement.fragment.*
import field.obj.life.FieldLife
import util.packet.PacketReader
import util.packet.PacketWriter
import java.awt.Point
import java.util.*

class MovePath(packet: PacketReader) : MoveFragment {

    private val fragments: MutableList<MoveFragment> = ArrayList()
    lateinit var position: Point
    lateinit var vposition: Point

    init {
        decode(packet)
    }

    override fun decode(packet: PacketReader) {
        position = packet.readPoint()
        vposition = packet.readPoint()

        val size = packet.readByte()
        for (i in 0 until size) {
            when (val movePathAttribute = packet.readByte()) {
                MovePathAttribute.NORMAL,
                MovePathAttribute.HANG_ON_BACK,
                MovePathAttribute.FALL_DOWN,
                MovePathAttribute.WINGS,
                MovePathAttribute.MOB_ATTACK_RUSH,
                MovePathAttribute.MOB_ATTACK_RUSH_STOP,
                -> fragments.add(NormalMoveFragment(movePathAttribute, packet))
                MovePathAttribute.JUMP,
                MovePathAttribute.IMPACT,
                MovePathAttribute.START_WINGS,
                MovePathAttribute.MOB_TOSS,
                MovePathAttribute.DASH_SLIDE,
                MovePathAttribute.MOB_LADDER,
                MovePathAttribute.MOB_RIGHT_ANGLE,
                MovePathAttribute.MOB_STOP_NODE_START,
                MovePathAttribute.MOB_BEFORE_NODE,
                -> fragments.add(JumpMoveFragment(movePathAttribute, packet))
                MovePathAttribute.FLASH_JUMP,
                MovePathAttribute.ROCKET_BOOSTER,
                MovePathAttribute.BACK_STEP_SHOT,
                MovePathAttribute.MOB_POWER_KNOCK_BACK,
                MovePathAttribute.VERTICAL_JUMP,
                MovePathAttribute.CUSTOM_IMPACT,
                MovePathAttribute.COMBAT_STEP,
                MovePathAttribute.HIT,
                MovePathAttribute.TIME_BOMB_ATTACK,
                MovePathAttribute.SNOWBALL_TOUCH,
                MovePathAttribute.BUFF_ZONE_EFFECT,
                -> fragments.add(ActionMoveFragment(movePathAttribute, packet))
                MovePathAttribute.IMMEDIATE,
                MovePathAttribute.TELEPORT,
                MovePathAttribute.ASSAULTER,
                MovePathAttribute.ASSASSINATION,
                MovePathAttribute.RUSH,
                MovePathAttribute.SIT_DOWN,
                -> fragments.add(TeleportMoveFragment(movePathAttribute, packet))
                MovePathAttribute.START_FALL_DOWN -> fragments.add(StartFallDownMoveFragment(movePathAttribute, packet))
                MovePathAttribute.FLYING_BLOCK -> fragments.add(FlyingBlockMoveFragment(movePathAttribute, packet))
                MovePathAttribute.STAT_CHANGE -> fragments.add(StatChangeMoveFragment(movePathAttribute, packet))
            }
        }
    }

    override fun apply(life: FieldLife) {
        fragments.forEach { it.apply(life) }
    }

    override fun encode(packet: PacketWriter) {
        packet.writePosition(position)
        packet.writePosition(vposition)
        packet.write(fragments.size)
        fragments.forEach { it.encode(packet) }
    }
}