package net.maple.handlers.user;

import client.Character;
import client.Client;
import field.object.portal.Portal;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class UserTransferFieldRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readByte(); // ?
        int id = reader.readInteger();

        if (id != -1) {
            if (c.isAdmin()) {
                chr.changeField(id);
                System.out.println("Hello! Moving " + chr.getName() + " to " + id);
            } else {
                c.close(this, "Using /m without admin acc");
            }
        } else {
            String portalName = reader.readMapleString();
            Portal portal = chr.getField().getPortalByName(portalName);

            portal.enter(chr);
        }
    }
}
