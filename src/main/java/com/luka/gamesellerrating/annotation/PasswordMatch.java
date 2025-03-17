package com.luka.gamesellerrating.annotation;

import com.luka.gamesellerrating.validation.PasswordMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {

    String message() default "Password fields don't match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
