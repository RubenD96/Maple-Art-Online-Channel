package field.object.portal;

import client.Character;
import field.Field;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FieldPortal extends AbstractFieldPortal implements Portal {

    @NonNull private Field field;

    @Override
    public void enter(Character chr) {
        if (getTargetMap() != 999999999) {
            Field field = chr.getChannel().getFieldManager().getField(getTargetMap());
            Portal portal = field.getPortalByName(getTargetName());

            portal.leave(chr);
        }
    }

    @Override
    public void leave(Character chr) {
        field.enter(chr, (byte) getId());
    }
}
