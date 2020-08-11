package scripting.portal;

import client.Client;
import field.object.portal.FieldPortal;
import lombok.Getter;
import lombok.NonNull;
import scripting.AbstractPlayerInteraction;

public class PortalScriptMethods extends AbstractPlayerInteraction {

    private final @Getter FieldPortal portal;

    public PortalScriptMethods(@NonNull Client c, FieldPortal portal) {
        super(c);
        this.portal = portal;
    }

    public void enter() {
        portal.forceEnter(c.getCharacter());
    }
}
