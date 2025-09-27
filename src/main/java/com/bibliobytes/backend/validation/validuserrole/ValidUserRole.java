package com.bibliobytes.backend.validation.validuserrole;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUserRoleValidator.class)
public @interface ValidUserRole {
    String message() default "Unknown user role";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
