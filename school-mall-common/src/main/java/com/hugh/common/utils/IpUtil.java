package com.hugh.common.utils;

/**
 * @author 52123
 * @since 2019/6/21 15:34
 */
public class IpUtil {
    public static Object[] parseStringToIp(String serviceAddress) throws Exception{
        Object[] objects = new Object[2];
        String[] strings = serviceAddress.split(":");
        objects[0] = strings[0];
        objects[1] = Integer.valueOf(strings[1]);
        return objects;
    }
}
