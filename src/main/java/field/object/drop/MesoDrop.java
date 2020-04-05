package field.object.drop;

import client.Character;
import field.object.FieldObject;
import lombok.Getter;
import lombok.NonNull;

public class MesoDrop extends AbstractFieldDrop {

    @Getter final int meso;

    public MesoDrop(@NonNull byte enterType, @NonNull byte leaveType, @NonNull int owner, @NonNull FieldObject source, int meso) {
        super(enterType, leaveType, owner, source);
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
        getField().leave(this);
        chr.gainMeso(meso);
    }
}
