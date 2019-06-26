## Redis分布式锁
### 1. SETNX KEY VALUE
SETNX是SET if Not eXists的简写，当该Key不存在时才能保存Value，所以可以利用该命令实现同一个方法在多台服务器上同一时刻只有一条线程可以执行。<br>
即当一个线程成功写入某键值对后，就获得了锁。执行完后可以删除该键或设置过期时间来释放锁<br>
缺点：在保存该键值对后，还没来得及设置过期时间或删除键时，持有该锁的服务器宕机了，会造成死锁

### 2. SET key value [expiration EX seconds| PX milli] [NX|XX]
https://redis.io/commands/set <br>
与SETNX具有相同的功能，不同的是，该命令保存键值对时还可以设置过期时间(原子操作)，即使获取了锁的服务器宕机了，也不会造成死锁<br>

### 3. 个人实现
用Spring Boot提供的redis starter，spring-data-redis-2.1版本依赖提供了带设置过期时间的SETNX来让开发者使用，超级方便。方法代码如下
```java
	/**
	 * Set {@code key} to hold the string {@code value} and expiration {@code timeout} if {@code key} is absent.
	 *
	 * @param key must not be {@literal null}.
	 * @param value must not be {@literal null}.
	 * @param timeout the key expiration timeout.
	 * @param unit must not be {@literal null}.
	 * @return {@literal null} when used in pipeline / transaction.
	 * @since 2.1
	 * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
	 */
	@Nullable
	Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit);
```

个人实现的获取锁和释放锁代码如下，测试暂无发现超卖现象
```java
    /**
     * 上锁，并设置键的过期时间防止死锁
     * 失败则进行重试
     *
     * @param maxRetries    重试次数
     * @param baseSleepTime 重试间隔时间
     * @param expireTime    键的过期时间
     * @return 是否成功获得锁
     */
    public boolean lock(int maxRetries, int baseSleepTime,
                        long expireTime, TimeUnit timeUnit) {
         /*
          * 参数校验
          */
        if (maxRetries < 0 || baseSleepTime < 0 || expireTime < 0) {
            return false;
        }
        /*
         * 通过RPC调用redis模块进行操作
         */
        boolean isLock = redisService.setIfAbsent(lockKey, "1", expireTime, timeUnit);
        if (isLock) {
            return true;
        } else {
           /*
            * 失败重试，自旋
            */
            while (maxRetries-- > 0) {
                try {
                    Thread.sleep(baseSleepTime);
                } catch (InterruptedException e) {
                    log.error(Thread.currentThread().getName() + "is interrupted", e);
                }
            }
        }
        return false;
    }
```



## RPC
 ### 1. RPC工作流程
    
<img src="./docs/rpc_flow_chart.jpg" />

RPC通信大致可划分为四个步骤<br>
1. 客户端发起请求：代理服务会封装请求的相关参数( requestID,methodName,ClassName,params等)，获取服务地址，最后将数据序列化后发给服务端
2. 服务端接收请求：接收到请求后，将参数反序列化，服务端会根据className去查找匹配对应的具体服务，并根据参数进行反射获取结果
3. 服务端响应请求：将对应的结果、请求id、消息等封装并序列化传输给客户端
4. 客户端接收响应：将响应的数据反序列化，得到返回结果，返回给调用处，结束

### 2. 序列化与反序列化
   JDK原生序列化之后的码流大、性能低，这里用的是protostuff序列化工具，可以看下ProtostuffSerializer那个类，以后闲的话再扩展其他序列化方式
    
### 3. 编码与解码
   用Netty进行TCP传输，可能会出现粘包半包的问题，这里通过在消息体前加入了4字节的消息头，声明消息体的长度来解决粘包半包问题。具体实现在RpcEncoder和RpcDecoder
   
### 4. 客户端xml配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:hugh="https://github.com/52123/schema/ch"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       https://github.com/52123/schema/ch
       https://github.com/52123/schema/ch.xsd">

    <hugh:client id="redisService" serviceInterface="com.hugh.common.rpc.RedisService"
                 serviceName="redis" zooKeeperAddress="127.0.0.1:2181"/>

</beans>
```

### 5. 服务端xml配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:hugh="https://github.com/52123/schema/ch"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       https://github.com/52123/schema/ch
       https://github.com/52123/schema/ch.xsd">

    <hugh:server id="redisService" serviceAddress="127.0.0.1"
                 servicePort="8080" serviceName="redis" zooKeeperAddress="127.0.0.1:2181"/>

</beans>
```