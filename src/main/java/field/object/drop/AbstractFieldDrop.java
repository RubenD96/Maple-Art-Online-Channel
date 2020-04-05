package field.object.drop;

import client.Character;
import field.object.AbstractFieldObject;
import field.object.FieldObject;
import field.object.FieldObjectType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.maple.packets.FieldPackets;
import util.packet.Packet;

@RequiredArgsConstructor
public abstract class AbstractFieldDrop extends AbstractFieldObject {

    @NonNull @Getter final byte enterType, leaveType;
    @NonNull @Getter final int owner;
    @NonNull @Getter final FieldObject source;
    @Getter @Setter long expire;

    public abstract boolean isMeso();

    public abstract int getInfo();

    public abstract void pickUp(Character chr);

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.DROP;
    }

    @Override
    public Packet getEnterFieldPacket() {
        return FieldPackets.enterField(this);
    }

    @Override
    public Packet getLeaveFieldPacket() {
        return FieldPackets.leaveField(this);
    }
}
