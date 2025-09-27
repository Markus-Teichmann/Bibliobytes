package com.bibliobytes.backend.validation.validdigitalid;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDigitalIdValidator.class)
public @interface ValidDigitalId {
    String message() default "Invalid digital id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
