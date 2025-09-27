package com.bibliobytes.backend.validation.emailsmatch;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailsMatchValidator.class)
public @interface EmailsMatch {
    String message() default "Emails do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
