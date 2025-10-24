package framework.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class FrontServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
    ChercherRessources(req, resp);
    }
        
    private void ChercherRessources(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path=req.getRequestURI().substring(req.getContextPath().length());
        
        // Si c'est la racine, servir index.html
        if(path.equals("/") || path.isEmpty()) {
            RequestDispatcher dispatcher = req.getRequestDispatcher("/index.html");
            dispatcher.forward(req, resp);
            return;
        }
        
        boolean resourceExists=getServletContext().getResource(path) !=null;

        if(resourceExists) {
            RequestDispatcher defaultDispatcher=getServletContext().getNamedDispatcher("default");
            defaultDispatcher.forward(req, resp);

        } else {
            resp.getWriter().println(path);
        }
    }
}
