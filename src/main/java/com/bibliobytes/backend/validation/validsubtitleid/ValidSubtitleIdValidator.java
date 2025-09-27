package com.bibliobytes.backend.validation.validsubtitleid;

import com.bibliobytes.backend.items.digitals.repositorys.SubtitleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ValidSubtitleIdValidator implements ConstraintValidator<ValidSubtitleId, Long> {
    private SubtitleRepository subtitleRepository;


    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        Set<Long> ids = subtitleRepository.findAllIds();
        return ids.contains(value);
    }
}
