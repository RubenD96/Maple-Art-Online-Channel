package field.movement;

import field.life.FieldLife;
import field.movement.fragment.*;
import lombok.Getter;
import lombok.Setter;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MovePath implements MoveFragment {

    private final List<MoveFragment> fragments;

    public MovePath(PacketReader packet) {
        this.fragments = new ArrayList<>();
        decode(packet);
    }

    @Getter @Setter Point position;
    @Getter @Setter Point vposition;

    @Override
    public void apply(FieldLife life) {
        fragments.forEach(fragment -> fragment.apply(life));
    }

    @Override
    public void decode(PacketReader packet) {
        position = packet.readPoint();
        vposition = packet.readPoint();

        byte size = packet.readByte();

        for (int i = 0; i < size; i++) {
            byte movePathAttribute = packet.readByte();

            switch (movePathAttribute) {
                case MovePathAttribute.NORMAL:
                case MovePathAttribute.HANG_ON_BACK:
                case MovePathAttribute.FALL_DOWN:
                case MovePathAttribute.WINGS:
                case MovePathAttribute.MOB_ATTACK_RUSH:
                case MovePathAttribute.MOB_ATTACK_RUSH_STOP:
                    fragments.add(new NormalMoveFragment(movePathAttribute, packet));
                    break;
                case MovePathAttribute.JUMP:
                case MovePathAttribute.IMPACT:
                case MovePathAttribute.START_WINGS:
                case MovePathAttribute.MOB_TOSS:
                case MovePathAttribute.DASH_SLIDE:
                case MovePathAttribute.MOB_LADDER:
                case MovePathAttribute.MOB_RIGHT_ANGLE:
                case MovePathAttribute.MOB_STOP_NODE_START:
                case MovePathAttribute.MOB_BEFORE_NODE:
                    fragments.add(new JumpMoveFragment(movePathAttribute, packet));
                    break;
                case MovePathAttribute.FLASH_JUMP:
                case MovePathAttribute.ROCKET_BOOSTER:
                case MovePathAttribute.BACK_STEP_SHOT:
                case MovePathAttribute.MOB_POWER_KNOCK_BACK:
                case MovePathAttribute.VERTICAL_JUMP:
                case MovePathAttribute.CUSTOM_IMPACT:
                case MovePathAttribute.COMBAT_STEP:
                case MovePathAttribute.HIT:
                case MovePathAttribute.TIME_BOMB_ATTACK:
                case MovePathAttribute.SNOWBALL_TOUCH:
                case MovePathAttribute.BUFF_ZONE_EFFECT:
                    fragments.add(new ActionMoveFragment(movePathAttribute, packet));
                    break;
                case MovePathAttribute.IMMEDIATE:
                case MovePathAttribute.TELEPORT:
                case MovePathAttribute.ASSAULTER:
                case MovePathAttribute.ASSASSINATION:
                case MovePathAttribute.RUSH:
                case MovePathAttribute.SIT_DOWN:
                    fragments.add(new TeleportMoveFragment(movePathAttribute, packet));
                    break;
                case MovePathAttribute.START_FALL_DOWN:
                    fragments.add(new StartFallDownMoveFragment(movePathAttribute, packet));
                    break;
                case MovePathAttribute.FLYING_BLOCK:
                    fragments.add(new FlyingBlockMoveFragment(movePathAttribute, packet));
                    break;
                case MovePathAttribute.STAT_CHANGE:
                    fragments.add(new StatChangeMoveFragment(movePathAttribute, packet));
                    break;
            }
        }
    }

    @Override
    public void encode(PacketWriter packet) {
        packet.writePosition(position);
        packet.writePosition(vposition);

        packet.write(fragments.size());
        fragments.forEach(fragment -> fragment.encode(packet));
    }
}
