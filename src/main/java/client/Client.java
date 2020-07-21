package client;

import client.party.Party;
import client.party.PartyMember;
import field.Field;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.Setter;
import net.maple.packets.ConnectionPackets;
import net.maple.packets.PartyPackets;
import net.netty.NettyClient;
import net.server.ChannelServer;
import net.server.MigrateInfo;
import net.server.Server;
import org.jooq.Record;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static database.jooq.Tables.ACCOUNTS;

public class Client extends NettyClient {

    private @Getter @Setter boolean admin;
    private @Getter int accId;
    private @Getter @Setter String accountName;
    private @Setter long lastPong, clientStart, lastNpcClick;
    ; // Not too sure what to do with these
    private @Getter boolean disconnecting = false, loggedIn = false, cc = false;
    private @Getter @Setter ChannelServer worldChannel;
    private @Getter @Setter Character character;
    private @Getter @Setter Set<String> macs, hwids, ips;
    private @Getter @Setter boolean banned;
    private @Getter Map<String, ScriptEngine> engines = new HashMap<>();
    private ScheduledFuture<?> ping;

    public Client(Channel c, byte[] siv, byte[] riv) {
        super(c, siv, riv);
    }

    public void login(Record data, MigrateInfo mi) {
        accId = data.getValue(ACCOUNTS.ID);
        banned = data.getValue(ACCOUNTS.BANNED) == 1;
        admin = data.getValue(ACCOUNTS.ADMIN) == 1;

        worldChannel = Server.getInstance().getChannels().get(mi.getChannel());
        worldChannel.getLoginConnector().messageLogin("1:" + accId);

        loggedIn = true;
    }

    public void startPing() {
        ping = ch.eventLoop().scheduleAtFixedRate(() -> ch.writeAndFlush(ConnectionPackets.getPing()),
                5, 5, TimeUnit.SECONDS);
    }

    public void cancelPingTask() {
        if (ping != null) {
            ping.cancel(true);
        }
    }

    public void executeIn(Runnable r, int seconds) {
        ch.eventLoop().schedule(r, seconds, TimeUnit.SECONDS);
    }

    public boolean canClickNPC() {
        return lastNpcClick + 500 < System.currentTimeMillis();
    }

    public void disconnect() {
        System.out.println("disconnecting");
        if (!disconnecting) {
            disconnecting = true;
            worldChannel.getLoginConnector().messageLogin("2:" + accId);
            if (ch.isOpen()) {
                close(this, "Disconnect function called");
            }

            loggedIn = false;
            if (character != null) {
                Field field = character.getField();
                if (field != null) {
                    field.leave(character);
                }
                notifyPartyLogout();
                character.getFriendList().notifyMutualFriends();
                worldChannel.removeCharacter(character);
                if (!cc) character.save();
                disconnecting = false;
            }
        }
    }

    public void changeChannel(ChannelServer channel) {
        this.cc = true;
        character.save();
        this.worldChannel = channel;
        migrate();
    }

    public void migrate() {
        Server.getInstance().getClients().get(accId).setChannel(worldChannel.getChannelId());
        write(ConnectionPackets.getChangeChannelPacket(worldChannel));
    }

    public void notifyPartyLogout() {
        Party party = character.getParty();
        if (party != null) {
            PartyMember member = party.getMember(character.getId());
            member.setCharacter(null);
            if (!cc) {
                List<PartyMember> online = party.getOnlineMembers();
                member.setOnline(false);
                member.setChannel(-2);
                if (online.size() > 1 && character.getId() == party.getLeaderId()) {
                    PartyMember newLeader = party.getRandomOnline(character.getId());
                    party.setLeaderId(newLeader.getCid());
                    for (PartyMember pmember : party.getMembers()) {
                        if (pmember.isOnline()) {
                            Character pm = Server.getInstance().getCharacter(pmember.getCid());
                            pm.write(PartyPackets.getTransferLeaderMessagePacket(newLeader.getCid(), true));
                            pm.write(PartyPackets.updateParty(party, pmember.getChannel()));
                        }
                    }
                } else {
                    for (PartyMember pmember : party.getMembers()) {
                        if (pmember.isOnline()) {
                            Character pm = Server.getInstance().getCharacter(pmember.getCid());
                            pm.write(PartyPackets.updateParty(party, pmember.getChannel()));
                        }
                    }
                }
            }
        }
    }
}