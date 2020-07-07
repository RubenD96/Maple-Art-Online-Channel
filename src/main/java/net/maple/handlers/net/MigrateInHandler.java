package net.maple.handlers.net;

import client.Character;
import client.Client;
import client.player.key.KeyBinding;
import field.Field;
import net.database.AccountAPI;
import net.database.CharacterAPI;
import net.database.ItemAPI;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.server.MigrateInfo;
import net.server.Server;
import org.jooq.Record;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

import java.util.Map;

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
                ItemAPI.loadInventories(chr);
                //chr.validateStats();

                Field field = c.getWorldChannel().getFieldManager().getField(chr.getFieldId());
                field.enter(chr);

                c.setCharacter(chr);
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
        return true; // todo
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
