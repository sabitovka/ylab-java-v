package io.sabitovka.controller;

import io.sabitovka.service.FooService;
import io.sabitovka.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/api/hello")
public class FooController {
    private final FooService fooService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sayHello() {
        String s = fooService.sayHello();
        return ResponseEntity.ok(new SuccessResponse<>(s));
    }
}
