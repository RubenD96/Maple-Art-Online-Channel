package net.server

import client.Character
import constants.ServerConstants.DROP_CLEAR_TIMER
import constants.ServerConstants.RESPAWN_TIMER
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.AttributeKey
import kotlinx.coroutines.*
import managers.FieldManager
import net.netty.PacketDecoder
import net.netty.PacketEncoder
import net.netty.ServerHandler
import util.packet.Packet
import java.net.BindException

class ChannelServer(val channelId: Int, var port: Int, val IP: String) : Thread() {

    var fieldManager: FieldManager = FieldManager()
    lateinit var centralListener: CentralListener
    private lateinit var bossGroup: EventLoopGroup
    private lateinit var workerGroup: EventLoopGroup

    fun init() {
        GlobalScope.launch {
            withContext(NonCancellable) {
                async { mobRespawnRoutine() }
                async { itemClearRoutine() }
            }
        }
    }

    private suspend fun mobRespawnRoutine() {
        delay(RESPAWN_TIMER)
        fieldManager.fields.values.forEach {
            if (it.getObjects<Character>().isNotEmpty()) {
                it.respawn()
            }
        }
        mobRespawnRoutine()
    }

    private suspend fun itemClearRoutine() {
        delay(DROP_CLEAR_TIMER)
        fieldManager.fields.values.forEach { it.removeExpiredDrops() }
        itemClearRoutine()
    }

    private fun startChannelServer() {
        bossGroup = NioEventLoopGroup()
        workerGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    public override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(
                            PacketDecoder(),
                            PacketEncoder(),
                            ServerHandler(port)
                        )
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

            // Bind and start to accept incoming connections.
            val f: ChannelFuture = b.bind(port).sync()
            println("Channel server started on $port")
            startCentralListener()
            f.channel().closeFuture().sync()
        } catch (be: BindException) {
            System.err.println("Port $port already in use, retrying with ${port + 1}")
            port++
            startChannelServer()
        } catch (ie: InterruptedException) {
            ie.printStackTrace()
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }

    override fun run() {
        init()
        startChannelServer()
    }

    private fun startCentralListener() {
        centralListener = CentralListener(this)
        centralListener.start()
    }

    override fun toString(): String {
        return "ChannelServer(channelId=$channelId, port=$port, IP='$IP')"
    }

    fun write(packet: Packet) {
        centralListener.channel.writeAndFlush(packet)
    }

    companion object {
        val CHANNEL_KEY: AttributeKey<ChannelServer> = AttributeKey.valueOf("C")
    }
}