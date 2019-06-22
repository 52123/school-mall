package com.hugh.rpc.server;

import com.hugh.common.threadpool.ThreadPoolFactory;
import com.hugh.rpc.protocol.RpcDecoder;
import com.hugh.rpc.protocol.RpcEncoder;
import com.hugh.rpc.protocol.RpcRequest;
import com.hugh.rpc.protocol.RpcResponse;
import com.hugh.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author 52123
 * @since 2019/6/21 14:42
 * 由bean的形式注入到业务中
 */
@Slf4j
@Component
public class NettyServer implements ApplicationContextAware, InitializingBean {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ThreadPoolExecutor threadPool = ThreadPoolFactory.getInstance("NettyServer-pool-%d");

    @Value("${zookeeper.serviceAddress}")
    private String serviceAddress;

    @Value("${zookeeper.servicePort}")
    private int servicePort;

    @Value("${zookeeper.serviceName}")
    private String serviceName;

    @Resource
    private ServiceRegistry serviceRegistry;

    private static HashMap<String,Object> serviceMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        for( Object serviceBean : serviceBeanMap.values()){
            Class[] classes = serviceBean.getClass().getInterfaces();
            for (Class clazz : classes){
                serviceMap.put(clazz.getName(), serviceBean);
                log.info("已加载服务类:{}",clazz.getName());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        threadPool.execute(()->{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcDecoder<>(RpcRequest.class))
                                         .addLast(new RpcEncoder<>(RpcResponse.class))
                                         .addLast(new NettyServerHandler(serviceMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 1024)
                      .childOption(ChannelOption.SO_KEEPALIVE, true);
            try {
                ChannelFuture future = serverBootstrap.bind(serviceAddress, servicePort).sync();
                log.info("启动服务成功 host:{}, port:{}",serviceAddress,serviceAddress);

                serviceRegistry.registerService(serviceName);
                log.info("注册服务成功 serviceName:{}",serviceName);
                future.channel().closeFuture().sync();
            }catch (Exception e){
                log.error("RPC Server start up failed",e);
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }
}