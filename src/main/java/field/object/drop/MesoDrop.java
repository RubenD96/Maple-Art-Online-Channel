package field.object.drop;

import client.Character;
import client.messages.MesoDropPickUpMessage;
import field.object.FieldObject;
import lombok.Getter;
import lombok.NonNull;
import net.maple.packets.CharacterPackets;

public class MesoDrop extends AbstractFieldDrop {

    @Getter final int meso;

    public MesoDrop(@NonNull byte enterType, @NonNull int owner, @NonNull FieldObject source, int meso) {
        super(enterType, owner, source);
        this.meso = meso;
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
