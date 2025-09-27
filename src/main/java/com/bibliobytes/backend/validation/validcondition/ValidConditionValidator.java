package com.bibliobytes.backend.validation.validcondition;

import com.bibliobytes.backend.donations.entities.Condition;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidConditionValidator implements ConstraintValidator<ValidCondition, Condition> {
    @Override
    public boolean isValid(Condition condition, ConstraintValidatorContext context) {
        for (Condition c: Condition.values()) {
            if (c == condition) {
                return true;
            }
        }
        return false;
    }
}
