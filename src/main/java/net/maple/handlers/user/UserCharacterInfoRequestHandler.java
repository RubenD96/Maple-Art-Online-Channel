package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.inventory.ItemInventoryType;
import client.inventory.slots.ItemSlot;
import field.object.FieldObjectType;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserCharacterInfoRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();
        reader.readInteger();
        Character target = (Character) chr.getField().getObject(FieldObjectType.CHARACTER, reader.readInteger());

        if (target != null) {
            chr.write(getCharacterInfo(target));
        }
    }

    private static Packet getCharacterInfo(Character target) {
        PacketWriter pw = new PacketWriter(32);

        pw.writeHeader(SendOpcode.CHARACTER_INFO);
        pw.writeInt(target.getId());
        pw.write(target.getLevel());
        pw.writeShort(target.getJob());
        pw.writeShort(target.getFame());

        pw.write(0);

        pw.writeMapleString("Chronos is");
        pw.writeMapleString("the best");

        pw.write(0); // pMedalInfo

        // pets
        pw.writeBool(false);

        pw.write(0); // taming mob

        int[] writeableWishlist = Arrays.stream(target.getWishlist()).filter(i -> i != 0).toArray();
        pw.write(writeableWishlist.length); // wishlist
        Arrays.stream(writeableWishlist).forEach(pw::writeInt);

        // MedalAchievementInfo::Decode
        pw.writeInt(0);
        pw.writeShort(0);

        // chairs
        List<ItemSlot> chairs = target.getInventories()
                .get(ItemInventoryType.INSTALL)
                .getItems()
                .values().stream()
                .filter(i -> i.getTemplateId() / 10000 == 301)
                .collect(Collectors.toList());
        pw.writeInt(chairs.size());
        chairs.forEach(chair -> pw.writeInt(chair.getTemplateId()));

        return pw.createPacket();
    }
}
