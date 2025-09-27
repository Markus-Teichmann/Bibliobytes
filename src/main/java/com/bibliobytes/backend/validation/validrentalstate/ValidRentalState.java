package com.bibliobytes.backend.validation.validrentalstate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRentalStateValidator.class)
public @interface ValidRentalState {
    String message() default "Unknown rental state";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
