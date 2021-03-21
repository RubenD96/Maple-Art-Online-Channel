package net.server

import constants.ServerConstants
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import net.netty.central.CentralPacketDecoder
import net.netty.central.CentralPacketEncoder
import net.netty.central.CentralServerHandler
import net.server.ChannelServer.Companion.CHANNEL_KEY
import java.net.ConnectException

class CentralListener(val server: ChannelServer, val restart: Boolean = false) : Thread() {

    lateinit var channel: Channel

    override fun run() {
        launch()
    }

    private fun launch() {
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
            channel.attr(CHANNEL_KEY).set(server)
            channel.closeFuture().sync()
        } catch (ce: ConnectException) {
            launch()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            group.shutdownGracefully()
        }
    }
}