package com.bibliobytes.backend.validation.ValidRentalId;

import com.bibliobytes.backend.rentals.RentalRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ValidRentalIdValidator implements ConstraintValidator<ValidRentalId, Long> {
    private RentalRepository rentalRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        Set<Long> ids = rentalRepository.findAllIds();
        return ids.contains(id);
    }
}
