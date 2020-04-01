package net.maple.handlers.user;

import client.Character;
import client.Client;
import field.object.drop.EnterType;
import field.object.drop.MesoDrop;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class UserDropMesoRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        reader.readInteger(); // ??
        int meso = reader.readInteger();

        if (meso <= chr.getMeso() && meso > 9 && meso < 50001) {
            chr.gainMeso(-meso);
            MesoDrop drop = new MesoDrop(EnterType.REGULAR, chr.getId(), chr, meso);
            drop.setPosition(chr.getPosition());
            chr.getField().enter(drop);
        }
    }
}
