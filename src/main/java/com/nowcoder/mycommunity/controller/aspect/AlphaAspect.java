package com.nowcoder.mycommunity.controller.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AlphaAspect {

    // the first * means every return type
    // the second * means all classes in the com.nowcoder.mycommunity.service package
    // the third * means all functions in those classes
    // (..) means all parameter type
    // pointcut
    @Pointcut("execution(* com.nowcoder.mycommunity.service.*.*(..))")
    public void pointcut() {

    }

    // do this function before pointcut()
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturn");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    // not only execute this function before pointcut, but after it
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object object = joinPoint.proceed();
        System.out.println("around after");
        return object;
    }
}
