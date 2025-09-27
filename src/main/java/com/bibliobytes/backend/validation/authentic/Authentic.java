package com.bibliobytes.backend.validation.authentic;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AuthenticValidator.class)
public @interface Authentic {
    String message() default "must be Authenticated";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}