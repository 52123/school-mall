package com.hugh.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author 52123
 * @since 2019/6/17 9:18
 * 根据方法的第一个参数对象的所有非空属性作为redisKey
 * 传入的key为空则直接拼接对象的所有非空属性值
 * 不为空则替换{} 为 对应的对象属性
 * 当readObject为false时，用key的值作为redisKey
 * 用法一  @ObjectKeyCache(key = "redisKey:{}:{}", fields ={"name","age"}) --> redisKey:Hugh:12
 * 若name为null， 则结果为 redisKey:12
 * 用法二  @ObjectKeyCache(fields ={"name","age"})  -->  Hugh:12
 * 用法三  @ObjectKeyCache(key = "redisKey"，readObject = false)   -->  redisKey
 * 用法四  @ObjectKeyCache   自动获取方法第一个对象的非空属性作为redisKey  -->  Hugh:12
 * 2019.6.26 新增过期功能
 * 1. @ObjectKeyCache(fields ={"name","age"}, expire = 10) 过期时间为10秒
 * 2. @ObjectKeyCache(fields ={"name","age"}, expire = 10，TimeUnit.HOURS) 10天过期
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectKeyCache {

    boolean readObject() default true;

    String key() default "";

    String[] fields() default {};

    long expire() default 0L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
