package com.bibliobytes.backend.validation.validuserrolename;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUserRoleNameValidator.class)
public @interface ValidUserRoleName {
    String message() default "Unknown user role name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
