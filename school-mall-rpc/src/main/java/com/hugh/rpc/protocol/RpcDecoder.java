package com.hugh.rpc.protocol;

import com.hugh.rpc.serialize.BaseSerializer;
import com.hugh.rpc.serialize.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author 52123
 * @since 2019/6/20 16:35
 * 解码器，将ByteBuf解码成pojo对象
 */
public class RpcDecoder<T> extends ByteToMessageDecoder {

    /**
     * 消息头占用了4个字节来保存消息体的大小
     */
    private static final int INT_BYTES = 4;

    private static final BaseSerializer SERIALIZER = ProtostuffSerializer.getInstance();

    private Class<T> genericClass;

    public RpcDecoder(Class<T> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        /*
         * 如果读取的字节小于4，则继续等待
         */
        if(in.readableBytes() < INT_BYTES){
            return;
        }

        /*
         *  标记读索引，若读取字节的长度小于消息体长度
         *  则重置读索引
         */
        in.markReaderIndex();

        int dataLength = in.readInt();

        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);
        out.add(SERIALIZER.deserialize(bytes,genericClass));
    }
}
