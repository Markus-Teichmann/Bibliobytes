package com.bibliobytes.backend.items.items.repositorys;

import com.bibliobytes.backend.items.items.entities.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends CrudRepository<Tag, Long> {
    //@EntityGraph(attributePaths = "items")
    Optional<Tag> findByName(String name);
    @Query(value = "select tag from tags tag, item_tags t where t.item_id = :itemId and t.tag_id = tag.id", nativeQuery = true)
    Set<Tag> findAllByItemId(@Param("itemId") Long itemId);
    @Query("select t.id from Tag t")
    Set<Long> findAllIds();
}
