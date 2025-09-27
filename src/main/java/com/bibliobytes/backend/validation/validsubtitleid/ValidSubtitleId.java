package com.bibliobytes.backend.validation.validsubtitleid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSubtitleIdValidator.class)
public @interface ValidSubtitleId {
    String message() default "Invalid Subtitle ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
