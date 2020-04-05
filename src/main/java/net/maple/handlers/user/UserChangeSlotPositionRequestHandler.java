package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.inventory.ItemInventoryType;
import client.inventory.slots.ItemSlot;
import client.inventory.slots.ItemSlotBundle;
import constants.ItemConstants;
import field.object.drop.ItemFieldDrop;
import net.maple.handlers.PacketHandler;
import net.maple.packets.CharacterPackets;
import util.packet.PacketReader;

public class UserChangeSlotPositionRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        Character chr = c.getCharacter();

        reader.readInteger();
        ItemInventoryType type = ItemInventoryType.values()[reader.readByte() - 1];
        short from = reader.readShort();
        short to = reader.readShort();
        short number = reader.readShort();

        if (to == 0) { // drop
            CharacterPackets.modifyInventory(chr,
                    i -> {
                        ItemSlot item = chr.getInventories().get(type).getItems().get(from);

                        if (!ItemConstants.isTreatSingly(item.getTemplateId())) {
                            if (!(item instanceof ItemSlotBundle)) return;
                            ItemSlotBundle bundle = (ItemSlotBundle) item;
                            if (bundle.getNumber() < number) return;

                            item = i.getInventoryContext(type).take(from, number);
                        } else {
                            i.getInventoryContext(type).remove(item);
                        }

                        ItemFieldDrop drop = new ItemFieldDrop((byte) 1, (byte) 1, chr.getId(), chr, item);
                        chr.getField().enter(drop);
                    }, true);
        } else {
            CharacterPackets.modifyInventory(chr,
                    i -> i.getInventoryContext(type).move(from, to),
                    true);
        }
    }
}
