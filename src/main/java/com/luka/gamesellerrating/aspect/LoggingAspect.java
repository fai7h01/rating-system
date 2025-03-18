package com.luka.gamesellerrating.aspect;

import com.luka.gamesellerrating.exception.UserNotFoundException;
import com.luka.gamesellerrating.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final AuthenticationService authService;

    @Pointcut("within(com.luka.gamesellerrating.controller.*)")
    public void anyControllerOperation() {}

    @Before("anyControllerOperation()")
    public void beforeAnyControllerOperationAdvice(JoinPoint joinPoint){
        log.info("Before -> Method: {}, User: {}",
                joinPoint.getSignature().toShortString(), getUsername());
    }

    @AfterReturning(value = "anyControllerOperation()", returning = "result")
    public void afterReturningAnyControllerOperationAdvice(JoinPoint joinPoint, Object result){
        log.info("After Returning -> Method: {}, User: {}, Result: {}",
                joinPoint.getSignature(), getUsername(), result.toString());
    }

    @AfterThrowing(value = "anyControllerOperation()", throwing = "exception")
    public void afterThrowingAnyControllerExceptionAdvice(JoinPoint joinPoint, Exception exception){
        log.error("After Throwing -> Method: {}, User: {}, Exception: {}",
                joinPoint.getSignature(), getUsername(), exception.getMessage());
    }

    private String getUsername(){
        if (authService.isUserAnonymous()) {
            return "anonymous";
        }
        return authService.getLoggedInUser().getUsername();
    }
}
