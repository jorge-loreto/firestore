package com.store.store.aop;

import org.springframework.stereotype.Component;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
@Component
public class LocationsAspect {

    // Log method calls
    @Before("execution(* com.store.store.service.ReferidosService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("LOG: Calling " + joinPoint.getSignature());
    }

    // Catch errors after method fails
    @AfterThrowing(pointcut = "execution(* com.store.store.service.ReferidosService.*(..))", throwing = "ex")
    public void handleError(Exception ex) {
        System.out.println("ERROR: " + ex.getMessage());
    }

    // Catch errors after method fails
    @After("execution(* com.store.store.service.ReferidosService.*(..))")
    public void loggerAfter(JoinPoint joinPoint) {
        System.out.println("LOG: loggerAfter methodCalling " + joinPoint.getSignature());
    }

}
