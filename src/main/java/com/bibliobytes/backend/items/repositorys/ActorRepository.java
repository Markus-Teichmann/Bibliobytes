package com.bibliobytes.backend.items.repositorys;

import com.bibliobytes.backend.items.entities.Actor;
import com.bibliobytes.backend.items.entities.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ActorRepository extends CrudRepository<Actor, Long> {
    Optional<Actor> findByName(String name);
}
