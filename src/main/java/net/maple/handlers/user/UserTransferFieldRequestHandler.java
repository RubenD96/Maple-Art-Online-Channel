package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.messages.broadcast.types.AlertMessage;
import field.object.portal.FieldPortal;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import util.packet.PacketReader;

public class UserTransferFieldRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        boolean cashShop = !reader.readBool();
        if (cashShop) {
            if (!chr.isInCashShop()) {
                c.close(this, "Not in CashShop");
                return;
            }
            chr.setInCashShop(false);
            c.migrate();
        } else {
            int id = reader.readInteger();

            if (id != -1) {
                if (c.isAdmin()) {
                    chr.changeField(id);
                } else {
                    c.close(this, "Using /m without admin acc");
                }
            } else {
                String portalName = reader.readMapleString();
                FieldPortal portal = chr.getField().getPortalByName(portalName);

                if (portal == null) {
                    chr.enableActions();
                    chr.write(CharacterPackets.message(new AlertMessage("There is a problem with the portal!\r\nName: " + portalName)));
                    return;
                }
                portal.enter(chr);
            }
        }
    }
}
