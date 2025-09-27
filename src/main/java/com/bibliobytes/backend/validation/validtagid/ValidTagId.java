package com.bibliobytes.backend.validation.validtagid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTagIdValidator.class)
public @interface ValidTagId {
    String message() default "Invalid tag id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
