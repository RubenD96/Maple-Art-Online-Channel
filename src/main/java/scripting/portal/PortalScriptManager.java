package scripting.portal;

import client.Client;
import client.messages.broadcast.types.AlertMessage;
import field.object.portal.FieldPortal;
import net.maple.packets.CharacterPackets;
import scripting.AbstractScriptManager;

import javax.script.Invocable;

public class PortalScriptManager extends AbstractScriptManager {

    private static final PortalScriptManager instance = new PortalScriptManager();

    private PortalScriptManager() {
    }

    public static PortalScriptManager getInstance() {
        return instance;
    }

    public void execute(Client c, FieldPortal portal, String script) {
        try {
            PortalScriptMethods portalScriptMethods = new PortalScriptMethods(c, portal);
            Invocable iv = getInvocable("portal/" + script + ".js", c);
            if (iv == null) {
                c.write(CharacterPackets.message(new AlertMessage("Portal script " + script + " does not exist")));
                System.out.println("Portal " + script + " is uncoded. (" + portal.getId() + ")");
                return;
            }
            engine.put("portal", portalScriptMethods);
            iv.invokeFunction("execute");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
