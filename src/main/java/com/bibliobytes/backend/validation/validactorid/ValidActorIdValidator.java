package com.bibliobytes.backend.validation.validactorid;

import com.bibliobytes.backend.items.digitals.repositorys.ActorRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class ValidActorIdValidator implements ConstraintValidator<ValidActorId, Long> {
    private ActorRepository actorRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        Set<Long> ids = actorRepository.findAllIds();
        return ids.contains(id);
    }
}
