package com.bibliobytes.backend.validation.ItemTypeNotBlank;


import com.bibliobytes.backend.items.items.entities.Type;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ItemTypeNotBlankValidator implements ConstraintValidator<ItemTypeNotBlank, Type> {
    @Override
    public boolean isValid(Type type, ConstraintValidatorContext context) {
        return !type.name().isEmpty();
    }
}
