package net.maple.handlers.user;

import client.Character;
import client.Client;
import field.Field;
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
            Field field = c.getWorldChannel().getFieldManager().getField(id);
            field.enter(chr);
        } else {
            String portalName = reader.readMapleString();
            Portal portal = chr.getField().getPortalByName(portalName);

            portal.enter(chr);
        }
    }
}
