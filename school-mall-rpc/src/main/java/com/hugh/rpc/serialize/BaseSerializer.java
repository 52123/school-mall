package com.hugh.rpc.serialize;

/**
 * @author 52123
 * @since 2019/6/20 15:42
 * 便于后续加入新的序列化工具
 */
public abstract class BaseSerializer {

    public abstract <T> byte[] serialize(T obj);

    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);

}
