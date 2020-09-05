package field.object.drop;

import client.Character;
import field.object.AbstractFieldObject;
import field.object.FieldObject;
import field.object.FieldObjectType;
import net.maple.packets.FieldPackets;
import util.packet.Packet;

public abstract class AbstractFieldDrop extends AbstractFieldObject {

    final int owner;
    final FieldObject source;
    final int questId;
    byte leaveType = 0x00;
    long expire;

    public AbstractFieldDrop(int owner, FieldObject source, int questId) {
        this.owner = owner;
        this.source = source;
        this.questId = questId;
    }

    public int getOwner() {
        return owner;
    }

    public FieldObject getSource() {
        return source;
    }

    public int getQuestId() {
        return questId;
    }

    public byte getLeaveType() {
        return leaveType;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

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
