package com.tss.AmlSystem.exception;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;

@Slf4j
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async error in method: {}", method.getName());
            log.error("Exception message: {}", ex.getMessage());
        };
    }
}
