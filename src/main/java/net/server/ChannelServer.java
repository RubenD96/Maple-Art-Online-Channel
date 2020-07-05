package net.server;

import constants.ServerConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import managers.FieldManager;
import net.netty.PacketDecoder;
import net.netty.PacketEncoder;
import net.netty.ServerHandler;

import java.util.Scanner;

@RequiredArgsConstructor
public class ChannelServer extends Thread {

    @NonNull @Getter final int channelId, port;
    @NonNull @Getter final String IP;
    @Getter FieldManager fieldManager;

    public void init() {
        fieldManager = new FieldManager();
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

            /*Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup wg = new NioEventLoopGroup();
            bootstrap.group(wg)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect(ServerConstants.IP, ServerConstants.LOGIN_PORT);
            System.out.printf("Connecting to %s/%d ... ", "127.0.0.1", ServerConstants.LOGIN_PORT);
            if (future.await().isSuccess()) {
                System.out.println("Connected!");
                Scanner in = new Scanner(System.in);
                in.nextLine().trim();
            }
*/
            // Wait until the net.server socket is closed.
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
