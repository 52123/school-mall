package com.hugh.rpc.serialize;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 52123
 * @since 2019/6/20 15:46
 */
public class ProtostuffSerializer extends BaseSerializer {

    /**
     * 构建schema的过程可能会比较耗时，因此希望使用过的类对应的schema能被缓存起来
     */
    private static Map<Class<?>, Schema<?>> cacheSchema = new ConcurrentHashMap<>();

    /**
     * class的newInstance无法满足某些对象的实例化
     * 用Objenesis的newInstance来绕开限制
     */
    private static Objenesis objenesis = new ObjenesisStd(true);

    /**
     * 单例模式 -- 懒汉
     */
    private volatile static ProtostuffSerializer protostuffSerializer = null;

    /**
     * 根据传入的对象，获得该对象的类对象
     * 根据类对象构建相对应Schema用于对象的序列化和反序列化、字段检验和字段序号映射
     * 最后通过ProtostuffIOUtil生成对象对应的字节数组，可用于网络传输
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, linkedBuffer);
        } finally {
            linkedBuffer.clear();
        }
    }

    /**
     *  根据传入的类对象和字节数组生成对应的对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        T obj = objenesis.newInstance(clazz);
        Schema schema = getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }


    /**
     * 先从缓存中查找类类型是否有对应Schema
     * 有则返回
     * 无则生成、存入Map再返回
     *
     * @param clazz 类类型
     * @param <T>   类对应的Schema
     */
    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> clazz) {
        /*
         * ConcurrentHashMap 1.8新语法
         * 若key对应的不存在，会将第二个值存入并返回
         */
        return (Schema<T>) cacheSchema.computeIfAbsent(clazz, RuntimeSchema::createFrom);
    }

    private ProtostuffSerializer(){}

    /**
     * 懒汉模式在多线程情况下不安全，采用双重校验锁
     * @return 全局唯一实例
     */
    public static ProtostuffSerializer getInstance(){
        if(protostuffSerializer == null){
            synchronized ("ProtostuffLock"){
                if(protostuffSerializer == null){
                    protostuffSerializer = new ProtostuffSerializer();
                }
                return protostuffSerializer;
            }
        }
        return protostuffSerializer;
    }
}
