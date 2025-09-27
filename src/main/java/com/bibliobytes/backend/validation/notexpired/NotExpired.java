package com.bibliobytes.backend.validation.notexpired;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotExpiredValidator.class)
public @interface NotExpired {
    String message() default "Token Expired";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
