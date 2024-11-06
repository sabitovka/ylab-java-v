package io.sabitovka.service;

import io.sabitovka.auth.entity.UserDetails;

public interface UserDetailsService {
    UserDetails findUserById(Long id);
}
