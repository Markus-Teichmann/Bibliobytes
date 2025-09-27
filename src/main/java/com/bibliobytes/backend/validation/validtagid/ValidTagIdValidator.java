package com.bibliobytes.backend.validation.validtagid;

import com.bibliobytes.backend.items.items.repositorys.TagRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ValidTagIdValidator implements ConstraintValidator<ValidTagId, Long> {
    private TagRepository tagRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        Set<Long> ids = tagRepository.findAllIds();
        return ids.contains(id);
    }
}
