package com.bibliobytes.backend.validation.validactorid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidActorIdValidator.class)
public @interface ValidActorId {
    String message() default "Invalid actor ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
