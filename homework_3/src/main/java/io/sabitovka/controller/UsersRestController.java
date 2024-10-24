package io.sabitovka.controller;

import io.sabitovka.dto.UserInfoDto;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.UserService;
import io.sabitovka.servlet.RestController;
import io.sabitovka.servlet.annotation.GetMapping;
import io.sabitovka.servlet.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/users")
public class UsersRestController implements RestController {
    private final UserService userService = ServiceFactory.getInstance().getUserService();

    @GetMapping("/{id|\\d+}")
    public UserInfoDto getUserById(String id) {
        return userService.findById(Long.parseLong(id));
    }

    @GetMapping("/blocked")
    public List<UserInfoDto> getBlockedUsers() {
        System.out.println("blocked");
        return userService.getBlockedUsers();
    }

    @GetMapping("/active")
    public List<UserInfoDto> getActiveUsers() {
        return userService.getActiveUsers();
    }
}
