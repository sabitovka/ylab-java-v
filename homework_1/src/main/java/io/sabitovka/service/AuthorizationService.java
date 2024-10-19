package io.sabitovka.service;

import io.sabitovka.model.User;

public interface AuthorizationService {
    User getCurrentUser();
    boolean isAdmin();
    Long getCurrentUserId();
    void login(String email, String password);
    void logout();
    boolean isLoggedIn();
}
