package com.bibliobytes.backend.items.digitals.repositorys;

import com.bibliobytes.backend.items.digitals.entities.Language;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface LanguageRepository extends CrudRepository<Language, Long> {
    Optional<Language> findByName(String name);
    @Query(value = "select lan from languages lan, digitals_languages d where d.digitals_id = :itemId and d.language_id = lan.id", nativeQuery = true)
    Set<Language> findAllByItemId(@Param("itemId") Long itemId);
    @Query("select l.id from Language l")
    Set<Long> findAllIds();
}
