package com.hugh.common.annotation.impl;

import com.hugh.common.annotation.ObjectKeyCache;
import com.hugh.common.rpc.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author 52123
 * @since 2019/6/17 9:24
 * AOP动态代理 + 反射实现
 */
@Component
@Aspect
@Slf4j
public class ObjectKeyCacheAspect {

    private static final int A = 65;
    private static final int Z = 90;
    private static final int LOWERCASE_A = 97;
    private static final int LOWERCASE_Z = 122;
    private static final String REGEX = "{}";
    private static final String REGEX_WITH_COLON = ":{}";

    private final RedisService redisService;

    public ObjectKeyCacheAspect(@Autowired RedisService redisService){
        this.redisService = redisService;
    }

    @Around("@annotation(com.hugh.common.annotation.ObjectKeyCache)")
    private Object around(ProceedingJoinPoint joinPoint) throws Throwable{

        // 获取注解所在类名
        Class<?> className = joinPoint.getTarget().getClass();

        // 获取注解所在的方法名
        String methodName = joinPoint.getSignature().getName();

        // 获取方法签名的类类型和参数类型
        Class<?>[] paramClass = ((MethodSignature)joinPoint.getSignature()).getParameterTypes();
        Object[] params = joinPoint.getArgs();

        String redisKey;
        try {
            // 得到访问方法的对象
            Method method = className.getMethod(methodName, paramClass);
            if(method.isAnnotationPresent(ObjectKeyCache.class)){
                ObjectKeyCache objectKeyCache = method.getAnnotation(ObjectKeyCache.class);
                // 若readObject为false，返回key，true，则拼接属性值作为key
                redisKey = objectKeyCache.readObject() ? getRedisKey(params, objectKeyCache) : getAnnotationKey(objectKeyCache);
                if(redisService.hasKey(redisKey)){
                    return redisService.getValue(redisKey);
                }else{
                    Object resp = joinPoint.proceed();
                    redisService.setValue(redisKey, resp);
                    return resp;
                }
            }
        }catch (Exception e){
            log.error("ObjectKeyCache error",e);
        }
        return null;
    }

    /**
     *  若方法无参数，直接返回注解中的key
     * @param objectKeyCache 注解
     * @return redisKey
     */
    private String getAnnotationKey(ObjectKeyCache objectKeyCache) throws Exception{
        String key = objectKeyCache.key();
        String[] fields = objectKeyCache.fields();

        if(fields.length != 0){
            throw new Exception("Empty method signature cannot have fields");
        }
        return key;
    }

    /**
     * 根据ObjectKeyCache生成key
     * @param params 方法签名
     * @param objectKeyCache 注解
     * @return redisKey
     */
    private String getRedisKey(Object[] params, ObjectKeyCache objectKeyCache) throws Exception{
        if(params.length == 0){
            throw new Exception("Method signature is not empty or you can set the readObject value to false");
        }

        Object param = params[0];
        String key = objectKeyCache.key();
        String[] fields = objectKeyCache.fields();

        // 注解没有值时，根据对象生成redisKey
        if(StringUtils.isBlank(key) && fields.length == 0){
            return generateDefaultKey(param);
        }

        // 只有fields时，根据提供的fields生成redisKey
        if(StringUtils.isBlank(key) && fields.length != 0){
            return generateFieldsKey(param, fields);
        }

        // 有key也有fields时
        if(StringUtils.isNotBlank(key) && fields.length != 0){
            // 若数量不匹配，抛出异常
            if(fields.length != StringUtils.countMatches(key,REGEX)){
                throw new Exception("{} does not match the number of fields");
            }
            return generateKey(param, key, fields);
        }

        throw new Exception("please see the annotation ObjectKeyCache to use it correctly");
    }

    /**
     *  生成自定义的redisKey
     * @param param 方法第一个签名
     * @param key 输入的key
     * @param fields 期望对象属性
     * @return redisKey
     */
    @SuppressWarnings("unchecked")
    private String generateKey(Object param, String key, String[] fields) throws Exception{
        Class clazz = param.getClass();
        StringBuilder redisKey = new StringBuilder(key);
        boolean hasInvalidValue = false;
        // 每测到一个{}则替换，若没有{}则直接返回
        for (String field : fields) {
            int index = redisKey.indexOf(REGEX);
            if (index == -1) {
                break;
            }
            Method method = clazz.getMethod("get" + firstCharToUpperCase(field));
            String value = String.valueOf(method.invoke(param));
            //若得到的值为null，则去父类那里看看是否有对应的属性值，若还是null，则跳过
            if("null".equals(value) || StringUtils.isBlank(value)){
                hasInvalidValue = true;
                continue;
            }
            redisKey.replace(index, index + 2, value);
        }

        return hasInvalidValue ? deleteRegex(redisKey.toString()) : redisKey.toString();
    }


    /**
     *  根据非空字段值生成redisKey
     *  如果方法没有参数、对象为null、字段全为空，则抛出异常
     * @return redisKey
     */
    @SuppressWarnings("unchecked")
    private String generateDefaultKey(Object param) throws Exception{

        Class clazz = param.getClass();
        StringBuilder redisKey = new StringBuilder();
        for(Field field : clazz.getFields()){
            // 获取到对象对应字段的get方法并从中获取值
            Method method = clazz.getMethod("get" + firstCharToUpperCase(field.getName()));
            // 不等于null时，添加到redisKey中
            String value = String.valueOf(method.invoke(param));
            if(!"null".equals(value) && StringUtils.isNotBlank(value)){
                redisKey.append(value).append(":");
            }
        }

        int length = redisKey.length();
        if(length == 0){
            throw new Exception("the first object must has at least one valid value");
        }

        // 删去最后一个冒号
        return redisKey.deleteCharAt(length - 1).toString();
    }

    /**
     *  根据对象属性生成相应的redisKey
     * @param param 方法签名对象
     * @param fields 字段
     * @return redisKey
     * @throws Exception 无对应属性时抛出异常
     */
    @SuppressWarnings("unchecked")
    private String generateFieldsKey(Object param, String[] fields) throws Exception{
        Class clazz = param.getClass();
        StringBuilder redisKey = new StringBuilder();
        for(String field : fields){
            // 获取到对象对应字段的get方法并从中获取值
            Method method = clazz.getMethod("get" + firstCharToUpperCase(field));
            // 不等于null时，添加到redisKey中
            String value = String.valueOf(method.invoke(param));
            if(!"null".equals(value) && StringUtils.isNotBlank(value)){
                redisKey.append(value).append(":");
            }
        }

        // 删去最后一个冒号
        return redisKey.deleteCharAt(redisKey.length() - 1).toString();
    }


    /**
     *  删去多余的{}
     * @param redisKey 带有多余{}的键
     * @return redisKey
     */
    private String deleteRegex(String redisKey) {
        // 除去多余的 :{}
        while(redisKey.contains(REGEX_WITH_COLON)){
            redisKey = redisKey.replace(REGEX_WITH_COLON, "");
        }

        while(redisKey.contains(REGEX)){
            redisKey =  redisKey.replace(REGEX,"");
        }

        return redisKey;
    }

    /**
     *  将传入字段的首字母转为大写
     * @param name 字段名
     * @return 首字母大写的字段名
     * @throws Exception 若首字符不为合法字母则抛出异常
     */
    private String firstCharToUpperCase(String name) throws Exception{
        char firstChar = name.charAt(0);

        //首字母为大写直接返回
        if(A <= firstChar &&  firstChar <= Z){
            return name;
        }

        if(LOWERCASE_A <= firstChar && firstChar <= LOWERCASE_Z){
            firstChar = (char)(firstChar - 32);
            return firstChar + name.substring(1);
        }

        throw new Exception("toUpperCase fail because the first char is an illegal letter");
    }
}

