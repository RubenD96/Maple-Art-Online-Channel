package net.maple.handlers.login;

import client.Character;
import client.Client;
import client.player.key.KeyBinding;
import field.Field;
import net.database.AccountAPI;
import net.database.CharacterAPI;
import net.database.ItemAPI;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import util.packet.Packet;
import util.packet.PacketReader;
import util.packet.PacketWriter;

public class MigrateInHandler extends PacketHandler {

    @Override
    public void handlePacket(PacketReader reader, Client c) {
        int cid = reader.readInteger();

        c.login(AccountAPI.getAccountInfoTemporary(cid));

        Character chr = CharacterAPI.getNewCharacter(c, cid);
        ItemAPI.loadInventories(chr);

        Field field = c.getWorldChannel().getFieldManager().getField(chr.getFieldId());
        field.enter(chr);

        c.setCharacter(chr);
        c.write(initFuncKey(chr));
        c.write(initQuickslot(chr));
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
