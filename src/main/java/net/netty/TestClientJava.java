package net.netty;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import util.crypto.MapleAESOFB;
import util.packet.Packet;
import util.packet.PacketReader;

import java.util.concurrent.locks.ReentrantLock;

public class TestClientJava {

    public static final AttributeKey<MapleAESOFB> CRYPTO_KEY = AttributeKey.valueOf("A");
    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");
    private byte[] siv;
    private byte[] riv;
    private int storedLength = -1;
    protected final Channel ch;
    private final ReentrantLock encodeLock;
    private final ReentrantLock migrateLock;
    private final PacketReader r;

    public TestClientJava(Channel c, byte[] alpha, byte[] delta) {
        ch = c;
        siv = alpha;
        riv = delta;
        r = new PacketReader();
        encodeLock = new ReentrantLock(true); // note: lock is fair to ensure logical sequence is maintained server-side
        migrateLock = new ReentrantLock();
    }

    public Channel getCh() {
        return ch;
    }

    public final PacketReader getReader() {
        return r;
    }

    public final int getStoredLength() {
        return storedLength;
    }

    public final void setStoredLength(int val) {
        storedLength = val;
    }

    public final byte[] getSendIV() {
        return siv;
    }

    public final byte[] getRecvIV() {
        return riv;
    }

    public final void setSendIV(byte[] alpha) {
        siv = alpha;
    }

    public final void setRecvIV(byte[] delta) {
        riv = delta;
    }

    public void write(Packet msg) {
        ch.writeAndFlush(msg);
    }

    public void close(Object cl, String reason) {
        System.out.println("[" + cl.getClass().getName() + "] " + reason + " - " + getIP());
        ch.flush().close();
    }

    public void close() {
        ch.flush().close();
    }

    public String getIP() {
        return ch.remoteAddress().toString().split(":")[0].substring(1);
    }

    public final void acquireEncoderState() {
        encodeLock.lock();
    }

    public final void releaseEncodeState() {
        encodeLock.unlock();
    }

    public final void acquireMigrateState() {
        if (!migrateLock.tryLock()) {
            releaseMigrateState();
            close(this, "User was already in migration");
        }
    }

    public final void releaseMigrateState() {
        migrateLock.unlock();
    }
}
