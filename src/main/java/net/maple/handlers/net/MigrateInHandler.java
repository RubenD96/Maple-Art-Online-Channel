package net.maple.handlers.net;

import client.Character;
import client.Client;
import client.player.key.KeyBinding;
import field.Field;
import net.database.*;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.server.MigrateInfo;
import net.server.Server;
import org.jooq.Record;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import static database.jooq.Tables.ACCOUNTS;

public class MigrateInHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        c.acquireMigrateState();
        try {
            int cid = reader.readInteger();
            Record accInfo = AccountAPI.getAccountInfoTemporary(cid);

            MigrateInfo mi = Server.getInstance().getClients().get(accInfo.getValue(ACCOUNTS.ID));
            if (mi.getIp().equals(c.getIP())) {
                //Server.getInstance().getClients().remove(accInfo.getValue(ACCOUNTS.ID));
                c.login(accInfo, mi);

                Character chr = CharacterAPI.getNewCharacter(c, cid);
                c.getWorldChannel().addCharacter(chr);
                c.setCharacter(chr);

                ItemAPI.loadInventories(chr);
                chr.validateStats();
                QuestAPI.loadAll(chr);

                TownsAPI.load(chr); // before entering field, in case of FirstVisit mapscript
                if (chr.getTowns().isEmpty()) {
                    chr.addTown(100);
                }

                Field field = c.getWorldChannel().getFieldManager().getField(chr.getFieldId());
                if (field == null) {
                    System.err.println("Invalid field id upon migrate " + chr.getFieldId());
                    field = c.getWorldChannel().getFieldManager().getField(1000);
                }
                field.enter(chr);

                FriendAPI.loadFriends(chr);
                FriendAPI.loadPending(chr);
                chr.getFriendList().sendPendingRequest();
                chr.loadParty();

                c.write(initFuncKey(chr));
                c.write(initQuickslot(chr));
            } else {
                c.close(this, "IP mismatch");
            }
        } finally {
            c.releaseMigrateState();
        }
    }

    @Override
    public boolean validateState(Client c) {
        return true;
    }

    private static Packet initFuncKey(Character chr) {
        PacketWriter pw = new PacketWriter(453);

        pw.writeHeader(SendOpcode.FUNC_KEY_MAPPED_INIT);
        pw.writeBool(false);

        for (int i = 0; i < 90; i++) {
            KeyBinding keyBinding = chr.getKeyBindings().get(i);
            byte type = 0;
            int action = 0;
            if (keyBinding != null) {
                type = keyBinding.getType();
                action = keyBinding.getAction();
            } else { // get default
                KeyBinding def = KeyBinding.getDefaultBindings()[i];
                if (def != null) {
                    type = def.getType();
                    action = def.getAction();
                }
            }
            pw.write(type);
            pw.writeInt(action);
        }

        return pw.createPacket();
    }

    private static Packet initQuickslot(Character chr) {
        PacketWriter pw = new PacketWriter(35);

        pw.writeHeader(SendOpcode.QUICKSLOT_MAPPED_INIT);
        pw.writeBool(true);
        for (int key : chr.getQuickSlotKeys()) {
            pw.writeInt(key);
        }

        return pw.createPacket();
    }
}
