package com.hugh.rpc.service;

import com.hugh.rpc.client.HelloService;
import com.hugh.rpc.server.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author 52123
 * @since 2019/6/23 16:01
 */
@RpcService
@Service
public class HelloServiceImpl2 implements HelloService {
    @Override
    public String saySomething(String something) {
        return "HelloServiceImpl2";
    }
}
