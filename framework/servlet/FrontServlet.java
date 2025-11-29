package framework.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import framework.annotation.AnnotationReader;
import framework.model.ModelView;
import framework.utilitaire.MappingInfo;

public class FrontServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String originalURI = (String) req.getAttribute("originalURI");
        String urlPath = originalURI != null ? originalURI : req.getRequestURI();

        String contextPath = req.getContextPath();
        String relativeUrl = urlPath.startsWith(contextPath)
                ? urlPath.substring(contextPath.length())
                : urlPath;

        System.out.println("Vous essayez d'acceder a : " + relativeUrl);

        Object result = dispatchToController(relativeUrl, req, resp);

        handleResult(result, req, resp);
    }

    /**
     * Trouve le mapping correspondant à l'URL et invoque la méthode du controller.
     */
    private Object dispatchToController(String urlPath, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException {

        MappingInfo mappingInfo = AnnotationReader.findMappingByUrl(urlPath);

        if (!mappingInfo.isFound()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new ServletException("Aucun mapping trouvé pour l'URL : " + urlPath);
        }

        try {
            Class<?> controllerClass = mappingInfo.getControllerClass();
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
            Method method = mappingInfo.getMethod();

            // Ici on suppose que la méthode prend HttpServletRequest en paramètre
            return method.invoke(controllerInstance, req);

        } catch (InstantiationException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new ServletException("Erreur lors de l'invocation du controller pour l'URL : " + urlPath, e);
        }
    }

    /**
     * Fonction qui vérifie le type de retour et agit en conséquence
     */
    private void handleResult(Object result, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (result instanceof String) {
            // String = chemin de la vue
            String viewPath = (String) result;
            RequestDispatcher dispatcher = req.getRequestDispatcher(viewPath);
            dispatcher.forward(req, resp);

        } else if (result instanceof ModelView) {

            ModelView mv = (ModelView) result;
            String viewPath = mv.getView();

            RequestDispatcher dispatcher = req.getRequestDispatcher(viewPath);
            dispatcher.forward(req, resp);

        } else {
            resp.setContentType("text/html");
            resp.getWriter().write("<h1>Type de retour non géré</h1>");
        }
    }
}