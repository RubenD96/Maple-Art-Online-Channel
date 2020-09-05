package field.object.drop;

import client.Character;
import client.messages.MesoDropPickUpMessage;
import field.object.FieldObject;
import net.maple.packets.CharacterPackets;

public class MesoDrop extends AbstractFieldDrop {

    final int meso;

    public MesoDrop(int owner, FieldObject source, int meso, int questId) {
        super(owner, source, questId);
        this.meso = meso;
    }

    public int getMeso() {
        return meso;
    }

    @Override
    public boolean isMeso() {
        return true;
    }

    @Override
    public int getInfo() {
        return meso;
    }

    @Override
    public void pickUp(Character chr) {
        leaveType = LeaveType.PICKUP;
        getField().leave(this, getLeaveFieldPacket(chr));
        chr.gainMeso(meso);
        chr.write(CharacterPackets.message(new MesoDropPickUpMessage(meso)));
    }
}
