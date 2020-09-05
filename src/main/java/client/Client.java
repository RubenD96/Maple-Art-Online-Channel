package client;

import client.interaction.storage.ItemStorage;
import client.inventory.slots.ItemSlotLocker;
import client.party.Party;
import client.party.PartyMember;
import field.Field;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import net.maple.packets.ConnectionPackets;
import net.maple.packets.GuildPackets;
import net.maple.packets.PartyPackets;
import net.netty.NettyClient;
import net.server.ChannelServer;
import net.server.MigrateInfo;
import net.server.Server;
import org.jooq.Record;

import javax.script.ScriptEngine;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static database.jooq.Tables.ACCOUNTS;

public class Client extends NettyClient {

    private boolean admin;
    private int accId;
    private String accountName;
    private long lastPong, clientStart, lastNpcClick;
    private boolean disconnecting = false, loggedIn = false, cc = false;
    private ChannelServer worldChannel;
    private String pic;
    private Character character;
    private Set<String> macs, hwids, ips;
    private boolean banned;
    private final Map<String, ScriptEngine> engines = new HashMap<>();
    private Integer cash;
    private ScheduledFuture<?> ping;
    private final List<ItemSlotLocker> locker = new ArrayList<>();
    private ItemStorage storage;

    public void setLastPong(long lastPong) {
        this.lastPong = lastPong;
    }

    public void setClientStart(long clientStart) {
        this.clientStart = clientStart;
    }

    public void setLastNpcClick(long lastNpcClick) {
        this.lastNpcClick = lastNpcClick;
    }

    public String getPic() {
        return pic;
    }

    public int getAccId() {
        return accId;
    }

    public boolean isDisconnecting() {
        return disconnecting;
    }

    public Map<String, ScriptEngine> getEngines() {
        return engines;
    }

    public List<ItemSlotLocker> getLocker() {
        return locker;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public ChannelServer getWorldChannel() {
        return worldChannel;
    }

    public void setWorldChannel(ChannelServer worldChannel) {
        this.worldChannel = worldChannel;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Set<String> getMacs() {
        return macs;
    }

    public void setMacs(Set<String> macs) {
        this.macs = macs;
    }

    public Set<String> getHwids() {
        return hwids;
    }

    public void setHwids(Set<String> hwids) {
        this.hwids = hwids;
    }

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public Integer getCash() {
        return cash;
    }

    public void setCash(Integer cash) {
        this.cash = cash;
    }

    public ItemStorage getStorage() {
        return storage;
    }

    public void setStorage(ItemStorage storage) {
        this.storage = storage;
    }

    public long getLastPong() {
        return lastPong;
    }

    public long getClientStart() {
        return clientStart;
    }

    public long getLastNpcClick() {
        return lastNpcClick;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isCc() {
        return cc;
    }

    public ScheduledFuture<?> getPing() {
        return ping;
    }

    public Client(Channel c, byte[] siv, byte[] riv) {
        super(c, siv, riv);
    }

    public void login(Record data, MigrateInfo mi) {
        accId = data.getValue(ACCOUNTS.ID);
        banned = data.getValue(ACCOUNTS.BANNED) == 1;
        admin = data.getValue(ACCOUNTS.ADMIN) == 1;
        pic = data.getValue(ACCOUNTS.PIC);

        worldChannel = Server.Companion.getInstance().getChannels().get(mi.getChannel());
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
                if (character.getGuild() != null && (!Server.Companion.getInstance().getClients().get(accId).getCashShop() || character.isInCashShop())) {
                    character.getGuild().getMembers().get(character.getId()).setOnline(false);
                    character.getGuild().broadcast(GuildPackets.getLoadGuildPacket(character.getGuild()));
                    GuildPackets.notifyLoginLogout(character.getGuild(), character, false);
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
        Server.Companion.getInstance().getClients().get(accId).setChannel(worldChannel.getChannelId());
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
                            Character pm = Server.Companion.getInstance().getCharacter(pmember.getCid());
                            pm.write(PartyPackets.getTransferLeaderMessagePacket(newLeader.getCid(), true));
                            pm.write(PartyPackets.updateParty(party, pmember.getChannel()));
                        }
                    }
                } else {
                    for (PartyMember pmember : party.getMembers()) {
                        if (pmember.isOnline()) {
                            Character pm = Server.Companion.getInstance().getCharacter(pmember.getCid());
                            pm.write(PartyPackets.updateParty(party, pmember.getChannel()));
                        }
                    }
                }
            }
        }
    }
}