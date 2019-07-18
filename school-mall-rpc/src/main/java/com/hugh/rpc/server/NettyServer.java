package com.hugh.rpc.server;

import com.hugh.rpc.protocol.RpcDecoder;
import com.hugh.rpc.protocol.RpcEncoder;
import com.hugh.rpc.protocol.RpcRequest;
import com.hugh.rpc.protocol.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;


/**
 * @author 52123
 * @since 2019/6/21 14:42
 * 由bean的形式注入到业务中
 */
@Slf4j
public class NettyServer {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private String serviceAddress;

    private int servicePort;

    private String serviceName;

    private ServiceRegistry serviceRegistry;

    private HashMap<String, Object> serviceMap = new HashMap<>();

    public NettyServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.serviceName = serviceRegistry.getServiceName();
        this.servicePort = serviceRegistry.getServicePort();
        this.serviceAddress = serviceRegistry.getServiceAddress();
    }

    public void initService(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Object serviceBean : serviceBeanMap.values()) {
            Class[] classes = serviceBean.getClass().getInterfaces();
            for (Class clazz : classes) {
                serviceMap.put(clazz.getName(), serviceBean);
                log.info("已加载服务类:{}", clazz.getName());
            }
        }
    }

    public void startUpService() {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new RpcDecoder<>(RpcRequest.class))
                                    .addLast(new RpcEncoder<>(RpcResponse.class))
                                    .addLast(new NettyServerHandler(serviceMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            try {
                ChannelFuture future = serverBootstrap.bind(serviceAddress, servicePort).sync();
                log.info("启动服务成功 host:{}, port:{}", serviceAddress, servicePort);

                serviceRegistry.registerService();
                log.info("注册服务成功 serviceName:{}", serviceName);
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("RPC Server start up failed", e);
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
    }
}