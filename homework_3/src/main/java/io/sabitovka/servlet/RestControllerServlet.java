package io.sabitovka.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sabitovka.controller.HabitsRestController;
import io.sabitovka.controller.StatisticRestController;
import io.sabitovka.controller.UsersRestController;
import io.sabitovka.servlet.annotation.GetMapping;
import io.sabitovka.servlet.annotation.RequestMapping;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@WebServlet("/api/*")
public class RestControllerServlet extends HttpServlet {
    private final Map<String, RestController> controllerMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        controllerMap.put("/users", new UsersRestController());
        controllerMap.put("/habits", new HabitsRestController());
        controllerMap.put("/statistics", new StatisticRestController());
    }

    private RestController findControllerByPath(String path) {
        if (path == null) {
            return null;
        }

        Optional<String> controllerPath = controllerMap.keySet().stream()
                .filter(path::startsWith)
                .findFirst();

        return controllerPath.map(controllerMap::get).orElse(null);
    }

    private Map<String, String> matchPath(String mappingPath, String urlPath) {
        Map<String, String> pathVariables = new HashMap<>();

        String[] mappingPathParts = mappingPath.split("/");
        String[] urlPathParts = urlPath.split("/");

        if (mappingPathParts.length != urlPathParts.length) {
            return null;
        }

        for (int i = 0; i < mappingPathParts.length; i++) {
            String mappingPathPart = mappingPathParts[i];
            String urlPathPart = urlPathParts[i];

            if (mappingPathPart.startsWith("{") && mappingPathPart.endsWith("}")) {
                String[] paramAndRegex = mappingPathPart.substring(1, mappingPathPart.length() - 1).split("\\|");
                String paramName = paramAndRegex[0];
                String regex = paramAndRegex.length > 1 ? paramAndRegex[1] : ".*";

                if (urlPathPart.matches(regex)) {
                    pathVariables.put(paramName, urlPathPart);
                } else {
                    return null;
                }
            } else if (!mappingPathPart.equals(urlPathPart)) {
                return null;
            }
        }

        return pathVariables;
    }

    private void writeResponse(HttpServletResponse response, Object result) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResult);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        RestController controller = findControllerByPath(pathInfo);

        if (controller == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Не найден контроллер для этого запроса");
            return;
        }

        RequestMapping requestMapping = controller.getClass().getAnnotation(RequestMapping.class);

        if (requestMapping == null || requestMapping.value() == null) {
            throw new RuntimeException();
        }

        Method[] methods = controller.getClass().getDeclaredMethods();
        for (Method method : methods) {
            GetMapping getMappingAnnotation = method.getAnnotation(GetMapping.class);
            if (getMappingAnnotation != null) {
                String mappingPath = requestMapping.value() + getMappingAnnotation.value();

                Map<String, String> pathVars = matchPath(mappingPath, pathInfo);

                if (pathVars == null) {
                    continue;
                }

                try {
                    Object[] args = pathVars.values().toArray();
                    Object result = method.invoke(controller, args);

                    writeResponse(resp, result);
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Не найден метод для текущего пути");
    }
}
