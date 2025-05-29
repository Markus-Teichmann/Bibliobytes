package com.bibliobytes.backend.repositorys;

import com.bibliobytes.backend.entities.Internal;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InternalRepository extends CrudRepository<Internal,UUID> {
    @EntityGraph(attributePaths = "external")
    @Query("SELECT i FROM Internal i WHERE i.id = :internalId")
    Optional<Internal> getInternalWithExternal(@Param("internalId") UUID internalId);

    @EntityGraph(attributePaths = "external")
    @Query("SELECT i FROM Internal i")
    List<Internal> getAll();

    @EntityGraph(attributePaths = "external")
    @Query("Select i From Internal i, External e Where i.id = e.id and e.email = :email")
    Optional<Internal> findByEmail(@Param("email") String email);

    boolean existsById(@Size(max = 16) UUID id);
}
