package net.server

import constants.ServerConstants
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import kotlinx.coroutines.*
import net.netty.central.CentralPacketDecoder
import net.netty.central.CentralPacketEncoder
import net.netty.central.CentralPackets
import net.netty.central.CentralServerHandler
import net.server.ChannelServer.Companion.CHANNEL_KEY
import java.net.ConnectException

class CentralListener(val server: ChannelServer, val restart: Boolean = false) : Thread() {

    lateinit var channel: Channel
    var pingTimer: Job? = null

    override fun run() {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val bootstrap = Bootstrap()
                .group(group)
                .channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    public override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(
                            CentralPacketDecoder(),
                            CentralPacketEncoder(),
                            CentralServerHandler()
                        )
                    }
                })
            channel = bootstrap.connect(ServerConstants.IP, 8888).sync().channel()

            pingTimer = GlobalScope.launch {
                async { ping() }
            }

            channel.attr(CHANNEL_KEY).set(server)
            channel.closeFuture().sync()
            pingTimer?.cancel()
        } catch (ce: ConnectException) {
            run()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            group.shutdownGracefully()
        }
    }

    private suspend fun ping() {
        delay(300000)
        server.write(CentralPackets.getPingPacket())
        ping()
    }
}