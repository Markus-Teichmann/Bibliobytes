package com.bibliobytes.backend.validation.validcondition;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidConditionValidator.class)
public @interface ValidCondition {
    String message() default "Not a valid condition";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
