package com.springboottest.config;

import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**

 */

/**
 * 没搞明白怎么配置, 留作备用, 不需要可以删除
 */
public class ExceptionHandleAsyncTaskExecutor implements AsyncTaskExecutor {

    /**
     * 持有线程池的引用
     */
    private AsyncTaskExecutor executor;

    public ExceptionHandleAsyncTaskExecutor(AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        //用独立的线程来包装，@Async其本质就是如此
        executor.execute(createWrappedRunnable(task), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        //用独立的线程来包装，@Async其本质就是如此。
        return executor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        //用独立的线程来包装，@Async其本质就是如此。
        return executor.submit(createCallable(task));
    }

    @Override
    public void execute(Runnable task) {
        executor.execute(createWrappedRunnable(task));
    }

    private <T> Callable createCallable(final Callable<T> task) {
        return new Callable() {
            public T call() throws Exception {
                try {
                    return task.call();
                } catch (Exception ex) {
                    handle(ex);
                    throw ex;
                }
            }
        };
    }

    private Runnable createWrappedRunnable(final Runnable task) {
        return new Runnable() {
            public void run() {
                try {
                    task.run();
                } catch (Exception ex) {
                    handle(ex);
                }
            }
        };
    }

    private void handle(Exception ex) {
        //具体的异常逻辑处理的地方
        System.err.println("Error during @Async execution: " + ex);
    }
}
