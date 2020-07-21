package field.object.drop;

import client.Character;
import client.inventory.item.templates.ItemTemplate;
import field.object.AbstractFieldObject;
import field.object.FieldObject;
import field.object.FieldObjectType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.maple.packets.FieldPackets;
import util.packet.Packet;

@Getter
@RequiredArgsConstructor
public abstract class AbstractFieldDrop extends AbstractFieldObject {

    @NonNull final int owner;
    @NonNull final FieldObject source;
    @NonNull final int questId;
    byte leaveType = 0x00;
    @Setter long expire;

    public abstract boolean isMeso();

    public abstract int getInfo();

    public abstract void pickUp(Character chr);

    @Override
    public FieldObjectType getFieldObjectType() {
        return FieldObjectType.DROP;
    }

    @Override
    public Packet getEnterFieldPacket() {
        return getEnterFieldPacket(EnterType.FFA);
    }

    public Packet getEnterFieldPacket(byte enterType) {
        return FieldPackets.enterField(this, enterType);
    }

    @Override
    public Packet getLeaveFieldPacket() {
        return getLeaveFieldPacket(null);
    }

    public Packet getLeaveFieldPacket(Character chr) {
        return FieldPackets.leaveField(this, chr);
    }
}
