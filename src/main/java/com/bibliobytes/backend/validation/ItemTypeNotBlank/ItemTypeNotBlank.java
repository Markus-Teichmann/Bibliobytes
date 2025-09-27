package com.bibliobytes.backend.validation.ItemTypeNotBlank;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ItemTypeNotBlankValidator.class)
public @interface ItemTypeNotBlank {
    String message() default "Item Type is required";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
