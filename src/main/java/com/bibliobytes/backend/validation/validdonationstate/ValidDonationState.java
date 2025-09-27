package com.bibliobytes.backend.validation.validdonationstate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDonationStateValidator.class)
public @interface ValidDonationState {
    String message() default "Invalid Donation State";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
