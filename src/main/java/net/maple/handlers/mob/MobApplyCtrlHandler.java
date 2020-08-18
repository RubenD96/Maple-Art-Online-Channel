package net.maple.handlers.mob;

import client.Character;
import client.Client;
import field.Field;
import field.object.FieldObjectType;
import field.object.life.FieldMob;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;

public class MobApplyCtrlHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        int oid = reader.readInteger();
        int distanceToPlayer = reader.readInteger();

        Character chr = c.getCharacter();
        if (chr == null) return;
        Field field = chr.getField();
        if (field == null) return;
        FieldMob mob = (FieldMob) field.getObject(FieldObjectType.MOB, oid);
        if (mob == null) return;

        //System.out.println("[MobApplyCtrlHandler] (" + chr.getName() + ") " + mob.getName() + " (" + oid + ") distance: " + distanceToPlayer);
        if (mob.getController() == null) {
            mob.setControllerDistance(distanceToPlayer);
            mob.setController(chr);
            field.updateControlledObjects();
        } else if (mob.getController().equals(chr)) {
            mob.setControllerDistance(distanceToPlayer);
        } else if (distanceToPlayer < mob.getControllerDistance()) {
            mob.setControllerDistance(distanceToPlayer);
            mob.setController(chr);
            field.updateControlledObjects();
        }
    }
}
