package net.maple.handlers.net

import client.Character
import client.Client
import client.player.key.KeyBinding.Companion.defaultBindings
import database.jooq.Tables
import net.database.AccountAPI.getAccountInfoTemporary
import net.database.CharacterAPI.getNewCharacter
import net.database.FriendAPI.loadFriends
import net.database.FriendAPI.loadPending
import net.database.ItemAPI.loadInventories
import net.database.QuestAPI
import net.database.TownsAPI
import net.database.WishlistAPI
import net.maple.SendOpcode
import net.maple.handlers.PacketHandler
import net.maple.packets.GuildPackets
import net.server.Server.clients
import util.packet.Packet
import util.packet.PacketReader
import util.packet.PacketWriter

class MigrateInHandler : PacketHandler {

    override fun handlePacket(reader: PacketReader, c: Client) {
        c.acquireMigrateState()
        try {
            val cid = reader.readInteger()

            val accInfo = getAccountInfoTemporary(cid)
            val mi = clients[accInfo.getValue(Tables.ACCOUNTS.ID)]
                    ?: return c.close(this, "MI is null (Channel reset?)")

            if (mi.ip == c.ip) {
                //Server.getInstance().getClients().remove(accInfo.getValue(ACCOUNTS.ID));
                c.login(accInfo, mi)
                val chr = getNewCharacter(c, cid)
                c.worldChannel.addCharacter(chr)
                c.character = chr

                loadInventories(chr)
                chr.validateStats()

                QuestAPI.loadAll(chr)
                WishlistAPI.load(chr)
                TownsAPI.load(chr) // before entering field, in case of FirstVisit mapscript

                if (chr.towns.isEmpty()) {
                    chr.addTown(100) // FM
                }

                chr.loadGuild()

                var field = c.worldChannel.fieldManager.getField(chr.fieldId)
                if (field == null) {
                    System.err.println("Invalid field id upon migrate " + chr.fieldId)
                    field = c.worldChannel.fieldManager.getField(1000)
                }
                chr.field = field!! // if field is null here, just quit derp
                field.enter(chr)

                loadFriends(chr)
                loadPending(chr)
                chr.friendList.sendPendingRequest()

                chr.loadParty()

                if (chr.guild != null) {
                    c.write(GuildPackets.getLoadGuildPacket(chr.guild))
                    if (!mi.cashShop) {
                        GuildPackets.notifyLoginLogout(chr.guild, chr, true)
                    }
                }

                c.write(initFuncKey(chr))
                c.write(initQuickslot(chr))

                mi.cashShop = false
            } else {
                c.close(this, "IP mismatch")
            }
        } finally {
            c.releaseMigrateState()
        }
    }

    override fun validateState(c: Client): Boolean {
        return true
    }

    companion object {
        private fun initFuncKey(chr: Character): Packet {
            val pw = PacketWriter(453)

            pw.writeHeader(SendOpcode.FUNC_KEY_MAPPED_INIT)
            pw.writeBool(false)

            for (i in 0 until 90) {
                val keyBinding = chr.keyBindings[i]
                var type: Byte = 0
                var action = 0
                if (keyBinding != null) {
                    type = keyBinding.type
                    action = keyBinding.action
                } else { // get default
                    val def = defaultBindings[i]
                    if (def != null) {
                        type = def.type
                        action = def.action
                    }
                }
                pw.write(type.toInt())
                pw.writeInt(action)
            }

            return pw.createPacket()
        }

        private fun initQuickslot(chr: Character): Packet {
            val pw = PacketWriter(35)

            pw.writeHeader(SendOpcode.QUICKSLOT_MAPPED_INIT)
            pw.writeBool(true)
            for (key in chr.quickSlotKeys) {
                pw.writeInt(key)
            }

            return pw.createPacket()
        }
    }
}

/*
package net.maple.handlers.net;

import client.Character;
import client.Client;
import client.player.key.KeyBinding;
import field.Field;
import net.database.*;
import net.maple.SendOpcode;
import net.maple.handlers.PacketHandler;
import net.maple.packets.GuildPackets;
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
            Record accInfo = AccountAPI.INSTANCE.getAccountInfoTemporary(cid);

            MigrateInfo mi = Server.INSTANCE.getClients().get(accInfo.getValue(ACCOUNTS.ID));
            if (mi == null) {
                c.close(this, "Channel reset");
                return;
            }

            if (mi.getIp().equals(c.getIp())) {
                //Server.getInstance().getClients().remove(accInfo.getValue(ACCOUNTS.ID));
                c.login(accInfo, mi);

                Character chr = CharacterAPI.INSTANCE.getNewCharacter(c, cid);
                c.getWorldChannel().addCharacter(chr);
                c.setCharacter(chr);

                ItemAPI.INSTANCE.loadInventories(chr);
                chr.validateStats();
                QuestAPI.INSTANCE.loadAll(chr);

                WishlistAPI.INSTANCE.load(chr);
                TownsAPI.INSTANCE.load(chr); // before entering field, in case of FirstVisit mapscript
                if (chr.getTowns().isEmpty()) {
                    chr.addTown(100);
                }

                chr.loadGuild();
                Field field = c.getWorldChannel().getFieldManager().getField(chr.getFieldId());
                if (field == null) {
                    System.err.println("Invalid field id upon migrate " + chr.getFieldId());
                    field = c.getWorldChannel().getFieldManager().getField(1000);
                }

                chr.field = field;
                field.enter(chr);

                FriendAPI.INSTANCE.loadFriends(chr);
                FriendAPI.INSTANCE.loadPending(chr);
                chr.getFriendList().sendPendingRequest();
                chr.loadParty();
                if (chr.getGuild() != null) {
                    c.write(GuildPackets.getLoadGuildPacket(chr.getGuild()));
                    if (!mi.getCashShop()) {
                        GuildPackets.notifyLoginLogout(chr.getGuild(), chr, true);
                    }
                }

                c.write(initFuncKey(chr));
                c.write(initQuickslot(chr));
                mi.setCashShop(false);
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
                KeyBinding def = KeyBinding.Companion.getDefaultBindings()[i];
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
 */