package com.bibliobytes.backend.items.repositorys;

import com.bibliobytes.backend.items.entities.Language;
import com.bibliobytes.backend.items.entities.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LanguageRepository extends CrudRepository<Language, Long> {
    Optional<Language> findByName(String name);
}
