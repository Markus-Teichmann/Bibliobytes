package com.bibliobytes.backend.items.digitals.repositorys;

import com.bibliobytes.backend.items.digitals.entities.Subtitle;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface SubtitleRepository extends CrudRepository<Subtitle, Long> {
    Optional<Subtitle> findByLanguage(String language);
    @Query(value = "select sub from subtitles sub, digitals_subtitles d where d.digitals_id = :itemId and d.subtitles_id = sub.id", nativeQuery = true)
    Set<Subtitle> findAllByItemId(@Param("itemId") Long itemId);
    @Query("select s.id from Subtitle s")
    Set<Long> findAllIds();
}
