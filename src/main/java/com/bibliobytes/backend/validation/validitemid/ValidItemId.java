package com.bibliobytes.backend.validation.validitemid;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidItemIdValidator.class)
public @interface ValidItemId {
    String message() default "Invalid Item ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
