package com.yuhao.weblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 自定义线程池：承载文章阅读量累加、PV 统计等异步任务
 */
@Configuration
public class ThreadPoolConfig {

    @Bean("asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        return Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "weblog-async-");
            t.setDaemon(true);
            return t;
        });
    }
}
