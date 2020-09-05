package field.object.portal;

import client.Character;
import client.messages.broadcast.types.AlertMessage;
import field.Field;
import net.maple.packets.CharacterPackets;
import scripting.portal.PortalScriptManager;

public class FieldPortal extends AbstractFieldPortal implements Portal {

    private final Field field;

    public FieldPortal(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public void enter(Character chr) {
        if (targetMap != 999999999) {
            if (!script.equals("")) {
                PortalScriptManager.getInstance().execute(chr.getClient(), this, script);
                chr.enableActions();
                return;
            }

            enterInternal(chr);
        }
    }

    public void forceEnter(Character chr) {
        if (targetMap != 999999999) {
            enterInternal(chr);
        }
    }

    private void enterInternal(Character chr) {
        Field field = chr.getChannel().getFieldManager().getField(targetMap);
        if (field == null) {
            error(chr);
            return;
        }

        Portal portal = field.getPortalByName(targetName);

        if (portal == null) {
            error(chr);
            return;
        }
        portal.leave(chr);
    }

    @Override
    public void leave(Character chr) {
        field.enter(chr, (byte) getId());
    }

    private void error(Character chr) {
        chr.enableActions();
        chr.write(CharacterPackets.message(
                new AlertMessage("There is a problem with the portal!" +
                        "\r\nID: " + id +
                        "\r\nTargetname: " + targetName))
        );
        System.err.println(this);
    }
}
