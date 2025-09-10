package com.bibliobytes.backend.items.repositorys;

import com.bibliobytes.backend.items.entities.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {
    //@EntityGraph(attributePaths = "items")
    Optional<Tag> findByName(String name);
}
