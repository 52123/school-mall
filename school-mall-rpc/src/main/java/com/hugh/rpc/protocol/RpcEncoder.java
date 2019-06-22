package com.hugh.rpc.protocol;

import com.hugh.rpc.serialize.BaseSerializer;
import com.hugh.rpc.serialize.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author 52123
 * @since 2019/6/20 14:57
 * 编码器，用于实现将pojo对象编码成ByteBuf
 * 以便通过网络进行传输
 */
public class RpcEncoder<T> extends MessageToByteEncoder {

    private Class<T> genericClass;

    private static BaseSerializer serializer = ProtostuffSerializer.getInstance();

    public RpcEncoder(Class<T> genericClass){
        this.genericClass = genericClass;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(genericClass.isInstance(msg)){
            byte[] data = serializer.serialize(msg);
            /*
             * 用消息头定义消息体长度的方式解决粘包半包问题
             * 消息头用4字节存储消息体的长度
             */
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
