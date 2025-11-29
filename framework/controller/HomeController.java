package framework.controller;

import framework.annotation.Controller;
import framework.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpServletRequest req) {
        req.setAttribute("nom", "Toniniaina");
        return "/index.jsp";   // vue à afficher
    }
}