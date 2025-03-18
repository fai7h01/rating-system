package com.luka.gamesellerrating.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceAspect {

    @Pointcut("@annotation(com.luka.gamesellerrating.annotation.ExecutionTime)")
    public void executionTimePC() {}

    @Around("executionTimePC()")
    public Object aroundAnyExecutionTimeAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        long beforeExecutionTime = System.currentTimeMillis();

        log.info("Execution Starts...");

        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("Error occurred while measuring execution time: {}", throwable.getMessage());
            throw throwable;
        } finally {
            long duration = System.currentTimeMillis() - beforeExecutionTime;
            log.info("Time taken to execute: {} ms, Method: {}",
                    duration, proceedingJoinPoint.getSignature().toShortString());
        }
    }
}