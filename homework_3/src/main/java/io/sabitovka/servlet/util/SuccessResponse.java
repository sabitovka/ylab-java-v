package io.sabitovka.servlet.util;

import lombok.Data;

@Data
public class SuccessResponse<T> {
    private int status = 200;
    private T data;

    public SuccessResponse(T data) {
        this.data = data;
    }
}
