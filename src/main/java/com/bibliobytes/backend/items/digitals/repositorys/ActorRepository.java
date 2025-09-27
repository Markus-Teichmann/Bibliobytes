package com.bibliobytes.backend.items.digitals.repositorys;

import com.bibliobytes.backend.items.digitals.entities.Actor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ActorRepository extends CrudRepository<Actor, Long> {
    Optional<Actor> findByName(String name);
    @Query(value = "select a from actors a, digitals_actors d where d.digitals_id = :itemId and d.actor_id = a.id", nativeQuery = true)
    Set<Actor> findAllByItemId(@Param("itemId") Long itemId);
    @Query("select a.id from Actor a")
    Set<Long> findAllIds();
}
