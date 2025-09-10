package com.bibliobytes.backend.items.repositorys;

import com.bibliobytes.backend.items.entities.Subtitle;
import com.bibliobytes.backend.items.entities.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubtitleRepository extends CrudRepository<Subtitle, Long> {
    Optional<Subtitle> findByLanguage(String language);
}
