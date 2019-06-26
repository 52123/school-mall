package com.hugh.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = "com.hugh.*")
@ImportResource("classpath:/application-rpc.xml")
public class SchoolMallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolMallSeckillApplication.class, args);
    }

}
