package net.maple.handlers.user;

import client.Client;
import field.object.portal.FieldPortal;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class UserPortalScriptRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        reader.readByte();

        String name = reader.readMapleString();
        FieldPortal portal = c.getCharacter().getField().getPortalByName(name);

        if (portal != null) {
            if (!portal.getScript().isEmpty()) {
                System.out.println(portal);
                c.getCharacter().enableActions();
            }
        }
    }
}
