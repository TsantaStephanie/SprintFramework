package framework.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import framework.model.ModelView; 

public class FrontServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String originalURI = (String) req.getAttribute("originalURI");
        String urlPath = originalURI != null ? originalURI : req.getRequestURI();
    
        System.out.println("Vous essayez d'acceder a : " + urlPath);
        
      
        Object result = processRequest(urlPath, req, resp);
        
      
        handleResult(result, req, resp);
    }
    
    /**
     * Fonction qui traite la requête et retourne soit un String soit un ModelView
     */
    private Object processRequest(String urlPath, HttpServletRequest req, HttpServletResponse resp) {
        if (urlPath.contains("/api/")) {
            return "Données API pour : " + urlPath;
        } else {
            ModelView mv = new ModelView();
            mv.setView("/testFramework/index.html"); 
            return mv;
        }
    }
    
    /**
     * Fonction qui vérifie le type de retour et agit en conséquence
     */
    private void handleResult(Object result, HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        if (result instanceof String) {
            resp.setContentType("text/html");
            resp.getWriter().write("<h1>" + result.toString() + "</h1>");
            
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