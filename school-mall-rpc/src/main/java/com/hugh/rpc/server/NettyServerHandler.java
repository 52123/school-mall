package com.hugh.rpc.server;

import com.hugh.rpc.utils.ThreadPoolFactory;
import com.hugh.rpc.protocol.RpcRequest;
import com.hugh.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 52123
 * @since 2019/6/21 15:23
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ThreadPoolExecutor threadPoolExecutor = ThreadPoolFactory.getInstance("NettyServerHandler-%d");

    private HashMap<String, Object> serviceMap;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        threadPoolExecutor.execute(() -> {
            RpcResponse response = handleRequest(request);
            ctx.writeAndFlush(response);
        });
    }


    public NettyServerHandler(HashMap<String, Object> map) {
        this.serviceMap = map;
    }

    private RpcResponse handleRequest(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] params = request.getParameters();
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, parameterTypes);
            response.setResult(method.invoke(serviceBean, params));
            response.setMsg("OK");
            return response;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            log.error("找不到该类或该方法", e);
            response.setMsg(e.getMessage());
            return response;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("反射获取结果失败", e);
            response.setMsg(e.getMessage());
            return response;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("服务连接发生异常", cause);
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
