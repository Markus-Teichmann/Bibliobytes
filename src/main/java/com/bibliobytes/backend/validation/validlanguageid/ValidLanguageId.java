package com.bibliobytes.backend.validation.validlanguageid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLanguageIdValidator.class)
public @interface ValidLanguageId {
    String message() default "Invalid language id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
