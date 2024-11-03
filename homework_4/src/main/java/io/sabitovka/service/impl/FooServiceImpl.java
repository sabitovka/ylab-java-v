package io.sabitovka.service.impl;

import io.sabitovka.service.FooService;
import org.springframework.stereotype.Service;

@Service
public class FooServiceImpl implements FooService {
    @Override
    public String sayHello() {
        return "Hello";
    }
}
