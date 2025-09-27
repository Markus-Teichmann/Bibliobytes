package com.bibliobytes.backend.validation.validbookid;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidBookIdValidator.class)
public @interface ValidBookId {
    String message() default "Invalid Book ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
