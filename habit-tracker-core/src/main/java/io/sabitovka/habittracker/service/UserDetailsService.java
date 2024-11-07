package io.sabitovka.habittracker.service;

import io.sabitovka.habittracker.auth.entity.UserDetails;

public interface UserDetailsService {
    UserDetails findUserById(Long id);
}
