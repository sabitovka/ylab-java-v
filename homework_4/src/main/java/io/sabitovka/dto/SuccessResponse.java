package io.sabitovka.dto;

import lombok.*;

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
