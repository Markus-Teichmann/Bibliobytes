package com.bibliobytes.backend.repositorys;

import com.bibliobytes.backend.entities.External;
import com.bibliobytes.backend.entities.Internal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExternalRepository extends CrudRepository<External, UUID> {
    @EntityGraph(attributePaths = "internal")
    @Query("SELECT e FROM External e WHERE e.id NOT IN (SELECT i.id FROM Internal i)")
    List<External> getAll();
    Optional<External> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsById(UUID id);
}
