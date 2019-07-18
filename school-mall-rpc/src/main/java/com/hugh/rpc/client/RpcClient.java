package com.hugh.rpc.client;

import com.hugh.rpc.protocol.RpcDecoder;
import com.hugh.rpc.protocol.RpcEncoder;
import com.hugh.rpc.protocol.RpcRequest;
import com.hugh.rpc.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 52123
 * @since 2019/6/21 2:14
 * RPC客户端 -- Reactor多线程模型
 */
@Slf4j
public class RpcClient {

    private EventLoopGroup group;

    private Channel channel;

    private static ConcurrentHashMap<Integer, RpcFuture> futureResponsePool = RpcFuturePool.getInstance();


    public RpcClient init(String serviceAddress, int servicePort) throws Exception {
        this.group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new RpcEncoder<>(RpcRequest.class))
                                .addLast(new RpcDecoder<>(RpcResponse.class))
                                .addLast(new RpcClientHandler());
                    }})
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        this.channel = bootstrap.connect(serviceAddress, servicePort).sync().channel();

        if (channel.isActive()) {
            log.info("连接成功, host: {}, port: {}", serviceAddress, servicePort);
        }
        return this;
    }


    public RpcFuture send(RpcRequest request) throws Exception {
        RpcFuture rpcFuture = new RpcFuture();
        futureResponsePool.put(request.getRequestId(), rpcFuture);
        this.channel.writeAndFlush(request).sync();
        return rpcFuture;
    }

}
