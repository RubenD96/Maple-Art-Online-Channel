package net.maple.handlers.user;

import client.Character;
import client.Client;
import client.interaction.storage.ItemStorageInteraction;
import client.inventory.ItemInventoryType;
import client.player.DbChar;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class UserStorageRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        StorageRequest request = StorageRequest.values()[reader.readByte()];
        Character chr = c.getCharacter();
        ItemStorageInteraction storage = chr.getActiveStorage();

        if (storage == null) return;

        if (request == StorageRequest.CLOSE_DIALOG) {
            storage.close(c);
            return;
        }

        PacketWriter pw = new PacketWriter(8);
        pw.writeHeader(SendOpcode.STORAGE_RESULT);
        switch (request) {
            case GET_ITEM: {
                ItemInventoryType type = ItemInventoryType.values()[reader.readByte() - 1];
                byte pos = reader.readByte();
                pw.write(storage.getItem(chr, type, pos).getValue());
                storage.encodeItems(pw);
            }
            break;
            case PUT_ITEM: {
                short pos = reader.readShort();
                int item = reader.readInteger();
                short count = reader.readShort();
                pw.write(storage.storeItem(chr, pos, item, count).getValue());
                storage.encodeItems(pw);
            }
            break;
            case SORT_ITEM:
                pw.write(storage.sortItems().getValue());
                storage.encodeItems(pw);
                break;
            case MONEY:
                int amount = reader.readInteger();
                pw.write(storage.transferMeso(chr, amount).getValue());
                storage.encodeItems(pw, DbChar.MONEY);
                break;
        }

        c.write(pw.createPacket());
    }

    private enum StorageRequest {
        LOAD(0x0),
        SAVE(0x1),
        CLOSE(0x2),
        CHECK_SSN_2(0x3),
        GET_ITEM(0x4),
        PUT_ITEM(0x5),
        SORT_ITEM(0x6),
        MONEY(0x7),
        CLOSE_DIALOG(0x8);

        private final int value;

        StorageRequest(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
