package net.maple.handlers.misc;

import client.Character;
import client.Client;
import field.obj.FieldObjectType;
import field.obj.drop.AbstractFieldDrop;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class DropPickUpRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        reader.readByte();
        reader.readInteger(); // timestamp?
        reader.readShort(); // start of position?
        reader.readShort();
        int id = reader.readInteger();
        reader.readInteger();

        AbstractFieldDrop drop = (AbstractFieldDrop) chr.getField().getObject(FieldObjectType.DROP, id);
        if (drop != null) {
            drop.pickUp(chr);
        } else {
            System.err.println("Picked up drop is null");
        }
    }
}
