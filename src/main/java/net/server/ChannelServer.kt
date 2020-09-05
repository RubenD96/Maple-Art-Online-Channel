package net.server

import client.Character
import constants.ServerConstants
import field.Field
import field.`object`.FieldObjectType
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import kotlinx.coroutines.*
import managers.FieldManager
import net.maple.handlers.user.UserChatHandler
import net.netty.PacketDecoder
import net.netty.PacketEncoder
import net.netty.ServerHandler
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap
import kotlin.collections.set

class ChannelServer(val channelId: Int, val port: Int, val IP: String) : Thread() {

    var fieldManager: FieldManager = FieldManager()
    var characters: MutableMap<Int, Character> = HashMap()
    var loginConnector: LoginConnector? = null

    fun init() {
        ServerConstants.COMMAND_LIST.add(ArrayList())
        ServerConstants.COMMAND_LIST.add(ArrayList())
        ServerConstants.COMMAND_LIST.add(ArrayList())
        UserChatHandler.refreshCommandList()
        GlobalScope.launch {
            withContext(NonCancellable) {
                async { mobRespawnRoutine() }
                async { itemClearRoutine() }
            }
        }
    }

    fun getCharacter(name: String): Character? {
        synchronized(characters) {
            return characters.values.stream().filter { c: Character -> (c.name == name) }.findFirst().orElse(null)
        }
    }

    fun getCharacter(id: Int): Character? {
        synchronized(characters) {
            return characters[id]
        }
    }

    fun addCharacter(chr: Character) {
        synchronized(characters) {
            characters[chr.id] = chr
        }
    }

    fun removeCharacter(chr: Character) {
        synchronized(characters) {
            characters.remove(chr.id)
        }
    }

    private suspend fun mobRespawnRoutine() {
        delay(5000L)
        fieldManager.fields.values.forEach(Consumer { field: Field ->
            if (field.getObjects(FieldObjectType.CHARACTER).isNotEmpty()) {
                field.respawn()
            }
        })
        mobRespawnRoutine()
    }

    private suspend fun itemClearRoutine() {
        delay(10000L)
        fieldManager.fields.values.forEach(Consumer { obj: Field -> obj.removeExpiredDrops() })
        itemClearRoutine()
    }

    override fun run() {
        init()
        val bossGroup: EventLoopGroup = NioEventLoopGroup()
        val workerGroup: EventLoopGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(object : ChannelInitializer<SocketChannel>() {
                        public override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(
                                    PacketDecoder(),
                                    PacketEncoder(),
                                    ServerHandler()
                            )
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

            // Bind and start to accept incoming connections.
            val f: ChannelFuture = b.bind(port).sync()
            println("Channel server started on $port")
            f.channel().closeFuture().sync()
        } catch (ie: InterruptedException) {
            ie.printStackTrace()
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }

    override fun toString(): String {
        return "ChannelServer(channelId=$channelId, port=$port, IP='$IP')"
    }
}