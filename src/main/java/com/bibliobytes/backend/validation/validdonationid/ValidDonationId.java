package com.bibliobytes.backend.validation.validdonationid;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDonationIdValidator.class)
public @interface ValidDonationId {
    String message() default "Invalid Donation ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
