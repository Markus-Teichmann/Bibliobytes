package com.bibliobytes.backend.validation.validitemid;

import com.bibliobytes.backend.items.items.repositorys.ItemRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ValidItemIdValidator implements ConstraintValidator<ValidItemId, Long> {
    private ItemRepository itemRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        System.out.println("ValidItemIdValidator: ");
        if (id == null) {
            System.out.println("Valid Id");
            return true;
        }
        Set<Long> ids = itemRepository.findAllIds();
        System.out.println("Valid Id: " + ids.contains(id));
        return ids.contains(id);
    }
}
