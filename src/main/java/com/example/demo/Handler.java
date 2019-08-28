package com.example.demo;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Handler
        implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(
            Throwable throwable, Method method, Object... obj) {

        System.out.println("Exception message - " + throwable.getMessage());
        System.out.println("Method name - " + method.getName());
        System.out.println("Cause "+ throwable.getCause());
        System.out.println("Trace "+ Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
        for (Object param : obj) {
            System.out.println("Parameter value - " + param);
        }
    }

}