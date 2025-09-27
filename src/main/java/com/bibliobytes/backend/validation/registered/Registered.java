package com.bibliobytes.backend.validation.registered;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegisteredValidator.class)
public @interface Registered {
    String message() default "You need to register first.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
