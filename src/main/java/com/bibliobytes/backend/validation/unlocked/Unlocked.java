package com.bibliobytes.backend.validation.unlocked;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UnlockedValidator.class)
public @interface Unlocked {
    String message() default "You need to be unlocked by an Admin.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
