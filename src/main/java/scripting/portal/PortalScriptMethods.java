package scripting.portal;

import client.Client;
import field.object.portal.FieldPortal;
import scripting.AbstractPlayerInteraction;

public class PortalScriptMethods extends AbstractPlayerInteraction {

    private final FieldPortal portal;

    public FieldPortal getPortal() {
        return portal;
    }

    public PortalScriptMethods(Client c, FieldPortal portal) {
        super(c);
        this.portal = portal;
    }

    public void enter() {
        portal.forceEnter(c.getCharacter());
    }
}
