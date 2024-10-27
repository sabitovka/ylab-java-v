package io.sabitovka.servlet.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SuccessResponse<T> {
    private int status = 200;
    private T data;

    public SuccessResponse(T data) {
        this.data = data;
    }

    public SuccessResponse(T data, int status) {
        this(data);
        this.status = status;
    }
}
