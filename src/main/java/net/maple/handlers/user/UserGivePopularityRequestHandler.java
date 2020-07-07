package net.maple.handlers.user;

import client.Character;
import client.Client;
import field.object.FieldObjectType;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class UserGivePopularityRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character sender = c.getCharacter();
        int cid = reader.readInteger();
        if (cid == sender.getId()) {
            c.close(this, c.getCharacter().getName() + " tried to fame themselves");
            return;
        }

        byte fame = reader.readByte();
        Character receiver = (Character) sender.getField().getObject(FieldObjectType.CHARACTER, cid);
        if (receiver == null) { // receiver is not on the same map (anymore)
            return;
        }
        if (fame == 0) { // defame
            System.out.println(sender.getName() + " defamed " + receiver.getName());
        } else if (fame == 1) { // fame
            System.out.println(sender.getName() + " famed " + receiver.getName());
        } else {
            c.close(this, "Invalid byte");
        }
    }
}
