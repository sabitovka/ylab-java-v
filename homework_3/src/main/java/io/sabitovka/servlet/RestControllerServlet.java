package io.sabitovka.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sabitovka.controller.AuthController;
import io.sabitovka.controller.HabitsRestController;
import io.sabitovka.controller.StatisticRestController;
import io.sabitovka.controller.UsersRestController;
import io.sabitovka.enums.ErrorCode;
import io.sabitovka.exception.ApplicationException;
import io.sabitovka.exception.ValidationException;
import io.sabitovka.servlet.annotation.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@WebServlet("/api/*")
public class RestControllerServlet extends HttpServlet {
    private final Map<String, RestController> controllerMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        controllerMap.put("/users", new UsersRestController());
        controllerMap.put("/habits", new HabitsRestController());
        controllerMap.put("/statistics", new StatisticRestController());
        controllerMap.put("/auth", new AuthController());
    }

    private RestController findControllerByPath(String path) {
        if (path == null) {
            return null;
        }

        return controllerMap.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }

    private Method[] getMappingMethodsOfController(RestController controller) throws IOException {
        if (controller == null) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "Не найден контроллер для этого запроса");
        }

        RequestMapping requestMapping = controller.getClass().getAnnotation(RequestMapping.class);

        if (requestMapping == null || requestMapping.value() == null) {
            throw new ApplicationException(ErrorCode.INTERNAL_ERROR);
        }

        return controller.getClass().getDeclaredMethods();
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

    private void processRequest(
            HttpServletRequest req,
            HttpServletResponse res,
            Class<? extends Annotation> annotationClass,
            Function<Method, String> getMethodMappingPath,
            boolean hasRequestBody
    ) throws IOException {
        String pathInfo = req.getPathInfo();
        RestController controller = findControllerByPath(pathInfo);
        Method[] methods = getMappingMethodsOfController(controller);

        for (Method method: methods) {
            if (!method.isAnnotationPresent(annotationClass)) {
                continue;
            }

            RequestMapping requestMapping = controller.getClass().getAnnotation(RequestMapping.class);
            String mappingPath = requestMapping.value() + getMethodMappingPath.apply(method);

            Map<String, String> pathVars = matchPath(mappingPath, pathInfo);
            if (pathVars == null) {
                continue;
            }


            Object requestBody = hasRequestBody ? getObjectFromRequest(req, method) : null;
            List<Object> args = new ArrayList<>(pathVars.values());
            if (requestBody != null) {
                args.add(0, requestBody);
            }

            try {
                Object result = method.invoke(controller, args.toArray());
                writeResponse(res, result);
                return;
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof ApplicationException) {
                    throw (ApplicationException) targetException;
                } else if (targetException instanceof ValidationException validationException) {
                    throw new ApplicationException(ErrorCode.BAD_REQUEST, validationException.getMessage());
                } else {
                    throw new RuntimeException(targetException);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new ApplicationException(ErrorCode.NOT_FOUND, "Не найден метод для текущего пути");
    }

    private Object getObjectFromRequest(HttpServletRequest req, Method method) throws IOException {
        BufferedReader reader = req.getReader();

        if (!reader.ready()) {
            return null;
        }

        Class<?> requestBodyClass = method.getParameters()[0].getType();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(reader, requestBodyClass);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp, GetMapping.class, (method) -> method.getAnnotation(GetMapping.class).value(), false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp, PostMapping.class, method -> method.getAnnotation(PostMapping.class).value(), true);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp, PutMapping.class, method -> method.getAnnotation(PutMapping.class).value(), true);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp, DeleteMapping.class, method -> method.getAnnotation(DeleteMapping.class).value(),false);
    }
}
