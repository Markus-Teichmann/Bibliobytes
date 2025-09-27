package com.bibliobytes.backend.validation.validexternalid;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidExternalIdValidator.class)
public @interface ValidExternalId {
    String message() default "Invalid External ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
