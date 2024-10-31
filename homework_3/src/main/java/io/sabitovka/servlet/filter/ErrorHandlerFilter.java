package io.sabitovka.servlet.filter;

import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.servlet.util.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Фильтр для обработки ошибок. Любую неизвестную или известную ошибку оборачивает в AppllicationException
 */
@WebFilter(
        filterName = "ErrorHandlingFilter",
        urlPatterns = "/api/*"
)
public class ErrorHandlerFilter extends HttpFilter {

    private void handleException(HttpServletRequest req, HttpServletResponse res, Exception e) throws IOException {
        if (e instanceof ApplicationException exception) {
            ServletUtils.writeJsonErrorResponse(res, exception);
            return;
        }
        if (e != null) {
            ServletUtils.writeJsonErrorResponse(res, new ApplicationException(ErrorCode.INTERNAL_ERROR, e));
        }
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {
        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            handleException(req, res, e);
        }
    }


}
