package com.bibliobytes.backend.validation.validlanguageid;

import com.bibliobytes.backend.items.digitals.repositorys.LanguageRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ValidLanguageIdValidator implements ConstraintValidator<ValidLanguageId, Long> {
    private LanguageRepository languageRepository;


    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        Set<Long> ids = languageRepository.findAllIds();
        return ids.contains(id);
    }
}
