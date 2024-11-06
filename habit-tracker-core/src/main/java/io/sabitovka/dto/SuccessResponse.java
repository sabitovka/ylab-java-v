package io.sabitovka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {
    private int status;
    private T data;

    public SuccessResponse(T data) {
        this(200, data);
    }
}
