package com.bibliobytes.backend.validation.validdigitalid;

import com.bibliobytes.backend.items.digitals.entities.Digital;
import com.bibliobytes.backend.items.items.entities.Item;
import com.bibliobytes.backend.items.items.repositorys.ItemRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidDigitalIdValidator implements ConstraintValidator<ValidDigitalId, Long> {
    private ItemRepository itemRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        Item item = itemRepository.findById(id).orElse(null);
        return item instanceof Digital;
    }
}
