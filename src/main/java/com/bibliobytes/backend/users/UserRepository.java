package com.bibliobytes.backend.users;

import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.users.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    List<User> findAllByRole(Role role);
    Optional<User> findByEmail(String email);
}
