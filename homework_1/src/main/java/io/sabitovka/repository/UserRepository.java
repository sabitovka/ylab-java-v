package io.sabitovka.repository;

import io.sabitovka.model.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<Long, User> {
    Optional<User> findUserByEmail(String email);
}
