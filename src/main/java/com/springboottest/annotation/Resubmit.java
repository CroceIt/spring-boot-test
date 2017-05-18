package com.springboottest.annotation;

import java.lang.annotation.*;

/**

 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resubmit {

    long value() default 0;

    /**
     * 指定多少时间以内不能重复提交
     * -1 表示不进行处理
     *
     * @return
     */
    long seconds();
}
