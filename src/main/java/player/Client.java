package player;

import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.Setter;
import net.maple.packets.ConnectionPackets;
import net.netty.NettyClient;
import net.server.ChannelServer;
import net.server.Server;
import org.jooq.Record;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static database.jooq.Tables.ACCOUNTS;

public class Client extends NettyClient {

    private @Getter @Setter byte gmLevel = 0;
    private @Getter int accId;
    private @Getter @Setter String accountName;
    private @Setter long lastPong, clientStart; // Not too sure what to do with these
    private @Getter boolean loggedIn = false;
    private @Getter @Setter ChannelServer worldChannel;
    private @Getter @Setter Character character;
    private @Getter @Setter Set<String> macs, hwids, ips;
    private @Getter @Setter boolean banned;
    private @Getter int loginTries;
    private ScheduledFuture<?> ping;

    public Client(Channel c, byte[] siv, byte[] riv) {
        super(c, siv, riv);
    }

    public void login(Record data) {
        accId = data.getValue(ACCOUNTS.ID);
        banned = data.getValue(ACCOUNTS.BANNED) == 1;

        worldChannel = Server.getInstance().getChannels().get(0);

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

    public void addLoginTry() {
        this.loginTries++;
    }
}