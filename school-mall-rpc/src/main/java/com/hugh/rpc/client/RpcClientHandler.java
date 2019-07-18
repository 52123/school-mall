package com.hugh.rpc.client;

import com.hugh.rpc.protocol.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 52123
 * @since 2019/6/20 17:57
 */
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private ConcurrentHashMap<Integer, RpcFuture> pendingRPC = RpcFuturePool.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        Integer requestId = response.getRequestId();
        RpcFuture rpcFuture = pendingRPC.get(requestId);
        if (rpcFuture != null) {
            pendingRPC.remove(requestId);
            rpcFuture.setResponse(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            channel.close();
        }
        log.error("服务连接发生异常", cause);
    }
}
