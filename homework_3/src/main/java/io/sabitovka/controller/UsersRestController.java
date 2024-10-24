package io.sabitovka.controller;

import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.UserService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.GetMapping;
import io.sabitovka.servlet.annotation.RequestMapping;

@RequestMapping("/users")
public class UsersRestController implements RestController {
    @GetMapping("/")
    public String getUsers() {
        return "1";
    }

    @GetMapping("/{id}")
    public String getUserById(String id) {
        return "2";
    }
}
