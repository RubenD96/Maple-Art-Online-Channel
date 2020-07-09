package net.server;

import client.Character;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import managers.FieldManager;
import net.netty.PacketDecoder;
import net.netty.PacketEncoder;
import net.netty.ServerHandler;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;

import static constants.ServerConstants.COMMAND_LIST;
import static net.maple.handlers.user.UserChatHandler.refreshCommandList;

@RequiredArgsConstructor
public class ChannelServer extends Thread {

    @NonNull @Getter final int channelId, port;
    @NonNull @Getter final String IP;
    @Getter FieldManager fieldManager;
    @Getter Map<String, Character> characters;
    @Getter @Setter LoginConnector loginConnector;

    public void init() {
        fieldManager = new FieldManager();
        COMMAND_LIST.add(new ArrayList<>());
        COMMAND_LIST.add(new ArrayList<>());
        COMMAND_LIST.add(new ArrayList<>());
        refreshCommandList();
        characters = new HashMap<>();
    }

    public Character getCharacter(String name) {
        return characters.get(name);
    }

    public Character getCharacter(int id) {
        return characters.values().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public void addCharacter(Character chr) {
        characters.put(chr.getName(), chr);
    }

    public void removeCharacter(Character chr) {
        characters.remove(chr.getName());
    }

    @Override
    public void run() {
        init();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new PacketDecoder(),
                                    new PacketEncoder(),
                                    new ServerHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Channel server started on " + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public String toString() {
        return "ChannelServer{" +
                "channelId=" + channelId +
                ", port=" + port +
                ", IP='" + IP + '\'' +
                '}';
    }
}
