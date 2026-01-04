package util.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

import com.google.gson.Gson;
import org.apache.commons.beanutils.ConvertUtils;

// Annotations personnalisées
import annotations.Param;
import annotations.PathVariable;

// Classes du projet
import util.http.ModelAndView;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ResponseHandler {
    private String responseBody;
    private final ServletContext context;

    public ResponseHandler(ServletContext context) {
        this.context = context;
    }

    // Fabrication jerijereo
    public void handleResponse(ClassMethod cm, HttpServletRequest req,HttpServletResponse res) throws IOException {
        Boolean cmExist = cm != null;
        System.out.println(cmExist);

        if(cmExist) {
            invokeControllerMethod(cm, req, res);
        } else {
            handle404(res);
        }

        if(responseBody != null) {
            try {
                PrintWriter out = res.getWriter();
                out.println(responseBody);
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    private void invokeControllerMethod(ClassMethod cm, HttpServletRequest req, HttpServletResponse res) {
        try {
            Class<?> c = cm.clazz;
            Method m = cm.method;
            m.setAccessible(true);

            Object[] args = getMatchedParams(m, req);
            Class<?> returnType = m.getReturnType();
            Object objectController = c.getDeclaredConstructor().newInstance();
            Object result = m.invoke(objectController, args);
            
            // Vérifier si la réponse doit être en JSON (basé sur l'en-tête Accept ou l'extension .json)
            boolean returnJson = req.getHeader("Accept") != null && 
                              (req.getHeader("Accept").contains("application/json") || 
                               req.getRequestURI().endsWith(".json"));
            
            if (returnJson || !returnType.equals(String.class) && !returnType.equals(ModelAndView.class)) {
                // Si c'est une requête API ou un type autre que String/ModelAndView, on renvoie du JSON
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                
                Gson gson = new Gson();
                
                // Si le résultat est déjà une ApiResponse, on l'utilise directement
                if (result instanceof ApiResponse) {
                    responseBody = gson.toJson(result);
                } 
                // Si c'est une Map, on la met dans une réponse API standard
                else if (result instanceof Map) {
                    ApiResponse apiResponse = ApiResponse.success(result);
                    responseBody = gson.toJson(apiResponse);
                }
                // Pour les autres types d'objets
                else if (result != null) {
                    // Si l'objet a une méthode toJson(), on l'utilise
                    try {
                        Method toJsonMethod = result.getClass().getMethod("toJson");
                        if (toJsonMethod != null && toJsonMethod.getReturnType().equals(String.class)) {
                            responseBody = (String) toJsonMethod.invoke(result);
                        }
                    } catch (NoSuchMethodException e) {
                        // Si pas de méthode toJson(), on sérialise l'objet directement
                        ApiResponse apiResponse = ApiResponse.success(result);
                        responseBody = gson.toJson(apiResponse);
                    }
                } else {
                    // Réponse vide
                    responseBody = gson.toJson(ApiResponse.success(null));
                }
                
                // Ajout du code de statut HTTP si disponible
                if (result instanceof ApiResponse) {
                    res.setStatus(((ApiResponse) result).getCode());
                }
            } 
            // Si c'est une chaîne, on la retourne telle quelle
            else if (returnType.equals(String.class)) {
                res.setContentType("text/plain");
                responseBody = result.toString();
            } 
            // Si c'est un ModelAndView, on traite les données et on forward vers la vue
            else if (returnType.equals(ModelAndView.class)) {
                ModelAndView mv = (ModelAndView) result;
                Map<String, Object> data = mv.getData();
                if (data != null) {
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
                String view = mv.getView();
                RequestDispatcher requestDispatcher = context.getRequestDispatcher(view);
                requestDispatcher.forward(req, res);
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
            // TODO: handle exception
            handleError(res, "Error invoking controller method: "+ ex.getMessage());
        } catch (ServletException | IOException ex) { // From requestDispatcher.forward()
            handleError(res, "Error forwarding to view: " + ex.getMessage());
        }
    }

private Object[] getMatchedParams(Method method, HttpServletRequest req) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        Map<String, String[]> paramsViaVue = req.getParameterMap();
        paramsViaVue.forEach((key, value) ->
            System.out.println(key + " => " + Arrays.toString(value))
        );

        Map<String, String> pathVariables = Map.of();
        String pathTemplate = (String) req.getAttribute("matchedRoute");
        if(pathTemplate != null) {
            String actualPath = req.getRequestURI().substring(req.getContextPath().length());
            pathVariables = extractPathVariables(pathTemplate, actualPath);
            pathVariables.forEach((key, value) ->
                System.out.println("PATH VARIABLE: " + key + " => " + value)
            );
        }
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            // @pathVarialble
            PathVariable pathVariable = p.getAnnotation(PathVariable.class);
            if(pathVariable != null) {
                String varName = pathVariable.value().isEmpty() ? p.getName() : pathVariable.value();
                if(pathVariables.containsKey(varName)) {
                    String value = pathVariables.get(varName);
                    Class<?> typeArg = p.getType();
                    Object convertedValue = ConvertUtils.convert(value, typeArg);
                    args[i] = convertedValue;
                } else {
                    args[i] = null;
                }
                continue;
            }
            // @param
            String paramName;
            Param annotation = p.getAnnotation(Param.class);
            if(annotation != null) {
                paramName = annotation.value();
            } else {
                paramName = p.getName();
            }
            System.out.println("PARAMNAME---- "+paramName);

            if(paramsViaVue.containsKey(paramName)) {
                String value = paramsViaVue.get(paramName)[0];
                Class<?> typeArg = p.getType();

                Object convertedValue = ConvertUtils.convert(value, typeArg);
                args[i] = convertedValue;
            } else {
                args[i] = null;
            }
        }
        return args;
    }

    private Map<String, String> extractPathVariables(String template, String path) {
        String[] tSeg = template.split("/");
        String[] pSeg = path.split("/");

        if(tSeg.length != pSeg.length) return Map.of();

        java.util.Map<String, String> map = new java.util.HashMap<>();
        for (int i = 0; i < tSeg.length; i++) {
            String ts = tSeg[i];
            String ps = pSeg[i];
            if(ts.startsWith("{") && ts.endsWith("}")) {
                String name = ts.substring(1, ts.length()-1);
                map.put(name, ps);
            } else if(!ts.equals(ps)) {
                // mismatch, return empty
                return Map.of();
            }
        }
        return map;
    }

    private void handleError(HttpServletResponse res, String error) {
        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseBody = formattedHtmlResponseBody("Error", "<h1>"+ error +"</h1>");
        res.setContentType("text/html;charset=UTF-8");
    }

    private void handle404(HttpServletResponse res) {
        String htmlBody = "<h1>404 not found</h1>";
        responseBody = formattedHtmlResponseBody("Method not found", htmlBody);
        res.setContentType("text/html;charset=UTF-8");
    }

    private String formattedHtmlResponseBody(String title, String body) {
    return String.format(
        "<!DOCTYPE html>" +
        "<html>" +
        "   <head><title>%s</title></head>" +
        "   <body>%s</body>" +
        "</html>", 
        title, body
    );
}
}
