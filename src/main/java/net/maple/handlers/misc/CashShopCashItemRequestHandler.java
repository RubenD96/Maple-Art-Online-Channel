package net.maple.handlers.misc;

import cashshop.types.CashItemRequest;
import client.Client;
import net.maple.handlers.PacketHandler;
import util.HexTool;
import util.packet.PacketReader;

public class CashShopCashItemRequestHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        byte type = reader.readByte();

        if (type == CashItemRequest.BUY.getValue()) {
            reader.readByte();
            int cashType = reader.readInteger();
            int commoditySN = reader.readInteger();
            System.out.println("[BUY] " + cashType + " - " + commoditySN);
        } else if (type == CashItemRequest.MOVE_L_TO_S.getValue()) {
            long sn = reader.readLong();
            System.out.println("[MOVE_L_TO_S] " + sn);
        } else if (type == CashItemRequest.MOVE_S_TO_L.getValue()) {
            long id = reader.readLong();
            System.out.println("[MOVE_S_TO_L] " + id);
        } else {
            System.err.println("[CashShopCashItemRequestHandler] Unhandled cash item operation " + type + "\n" + HexTool.toHex(reader.getData()));
        }
    }
}
