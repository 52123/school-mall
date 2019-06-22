package com.hugh.rpc.service;

import com.hugh.rpc.client.HelloService;
import com.hugh.rpc.server.RpcService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author 52123
 * @since 2019/6/21 16:33
 */
@RpcService
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String saySomething(String something) {
        return "got it";
    }
}
