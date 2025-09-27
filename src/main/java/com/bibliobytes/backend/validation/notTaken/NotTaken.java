package com.bibliobytes.backend.validation.notTaken;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotTakenValidator.class)
public @interface NotTaken {
    String message() default "Allready Taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}