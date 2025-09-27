package com.bibliobytes.backend.validation.validdonationid;

import com.bibliobytes.backend.donations.DonationRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ValidDonationIdValidator implements ConstraintValidator<ValidDonationId, Long> {
    private DonationRepository donationRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        Set<Long> ids = donationRepository.findAllIds();
        return ids.contains(id);
    }
}
