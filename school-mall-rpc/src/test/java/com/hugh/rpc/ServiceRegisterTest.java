package com.hugh.rpc;

import com.hugh.rpc.utils.ThreadPoolFactory;
import com.hugh.rpc.client.HelloService;
import com.hugh.rpc.client.RpcProxy;
import com.hugh.rpc.registry.ServiceDiscover;
import com.hugh.rpc.registry.ServiceRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 52123
 * @since 2019/6/20 0:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ServiceRegisterTest {

    @Resource
    private ServiceRegistry registry;

    @Resource
    private ServiceDiscover discover;

    @Resource
    private RpcProxy rpcProxy;

    private ThreadPoolExecutor threadPoolExecutor = ThreadPoolFactory.getInstance("test-%d");

    @Test
    public void test() throws  Exception{

        registry.registerService("order");

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void discover() throws Exception{
        String s = discover.getServiceByName("order");
        System.out.println(s);
    }


    @Test
    public void testClient()throws Exception{
        Thread.sleep(1000);

//        HelloService service = rpcProxy.create("testRPC",HelloService.class);
        threadPoolExecutor.execute(()->{
            HelloService service = rpcProxy.create("testRPC",HelloService.class);
            String result = service.saySomething("ssss");
            System.out.println(result);
        });

            Thread.sleep(Integer.MAX_VALUE);
    }
}
