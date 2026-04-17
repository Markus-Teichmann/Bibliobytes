package com.bibliobytes.backend.validation.taken;

import com.nimbusds.jose.Payload;
import jakarta.validation.Constraint;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TakenValidator.class)
public @interface Taken {
    String message() default "Taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
