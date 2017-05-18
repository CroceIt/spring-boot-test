package com.springboottest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**

 */
@Configuration
// 开启异步操作(多线程)
@EnableAsync
public class ExecutorConfig {

    /**
     * 设置线程池基本大小值, 线程池维护线程的最少数量
     */
    private int corePoolSize = 10;
    /**
     * 设置线程池最大值
     */
    // 虽然线程池大小是200, 但是使用多线程的地方限制为20了
    private int maxPoolSize = 200;
    /**
     * 线程池所使用的缓冲队列
     */
    // 在这个项目中我们需要缓冲队列, 否则不好记录正在执行的线程数
    private int queueCapacity = 0;
    /**
     * 配置线程最大空闲时间
     */
    private int keepAliveSeconds = 50000;

//    @Bean
//    public Executor mySimpleAsync() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(corePoolSize);
//        executor.setMaxPoolSize(maxPoolSize);
//        executor.setQueueCapacity(queueCapacity);
//        executor.setKeepAliveSeconds(keepAliveSeconds);
//        executor.setThreadNamePrefix("MySimpleExecutor-");
//        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
//        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
//    }

    @Bean
    public Executor myAsync() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("MyExecutor-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
