package io.sabitovka.servlet.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.sabitovka.dto.ErrorDto;
import io.sabitovka.exception.ApplicationException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

import java.io.IOException;

/**
 * Утилитарный класс для работы с сервлетами
 */
@UtilityClass
public class ServletUtils {
    public static void writeJsonResponse(HttpServletResponse response, Object result) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonResult = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResult);
    }

    public static void writeJsonErrorResponse(HttpServletResponse response, ApplicationException exception) throws IOException {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setInternalCode(exception.getErrorCode().getId());
        errorDto.setStatus(exception.getErrorCode().getHttpCode());
        errorDto.setError(exception.getErrorCode().getMessage());
        errorDto.setMessage(exception.getMessage());

        response.setStatus(exception.getErrorCode().getHttpCode());

        writeJsonResponse(response, errorDto);
    }
}