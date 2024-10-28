package io.sabitovka.controller;

import io.sabitovka.exception.ApplicationException;
import io.sabitovka.factory.ServiceFactory;
import io.sabitovka.service.UserService;
import io.sabitovka.util.logging.annotation.Loggable;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hello/*")
@Loggable
public class FooController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.printf("""
                Request:
                PathInfo: %s,
                ContextPath: %s
                QueryString: %s
                RequestUrl: %s
                ServletPath: %s
                """,
                req.getPathInfo(), req.getContextPath(), req.getQueryString(), req.getRequestURL(), req.getServletPath()
        );

        resp.setContentType("text/plain;charset=UTF-8");

        HttpServletMapping mapping = req.getHttpServletMapping();

        String mapName = mapping.getMappingMatch().name();
        String value = mapping.getMatchValue();
        String pattern = mapping.getPattern();
        String servletName = mapping.getServletName();

        StringBuilder builder = new StringBuilder();
        builder.append("Mapping type: ").append(mapName)
                .append("; Match value: ").append(value)
                .append("; Pattern: ").append(pattern)
                .append("; Servlet name: ").append(servletName);

        ServletOutputStream out = resp.getOutputStream();
        out.println(builder.toString());
    }
}
