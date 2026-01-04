package api;

import util.http.ApiResponse;
import util.http.ModelAndView;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.*;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    
    // Simule une base de données d'utilisateurs
    private static final Map<Integer, Map<String, Object>> users = new HashMap<>();
    private static int lastId = 0;
    
    // Initialisation des données de test
    public UserController() {
        if (users.isEmpty()) {
            // Données de test
            addUser(createUserMap("John Doe", "john@example.com"));
            addUser(createUserMap("Jane Smith", "jane@example.com"));
        }
    }
    
    // Récupérer tous les utilisateurs
    @GET
    @Path("")
    public ApiResponse getAllUsers() {
        return ApiResponse.success(users);
    }
    
    // Récupérer un utilisateur par ID
    @GET
    @Path("/{id}")
    public Object getUserById(@PathParam("id") int id) {
        if (!users.containsKey(id)) {
            return ApiResponse.notFound("Utilisateur non trouvé");
        }
        return ApiResponse.success(users.get(id));
    }
    
    // Créer un nouvel utilisateur
    @POST
    @Path("")
    public ApiResponse createUser(@FormParam("name") String name, 
                                @FormParam("email") String email) {
        Map<String, Object> newUser = createUserMap(name, email);
        int id = addUser(newUser);
        
        // Retourne l'utilisateur créé avec le statut 201 Created
        return ApiResponse.created(Collections.singletonMap("id", id));
    }
    
    // Mettre à jour un utilisateur
    @PUT
    @Path("/{id}")
    public ApiResponse updateUser(@PathParam("id") int id,
                                @FormParam("name") String name,
                                @FormParam("email") String email) {
        if (!users.containsKey(id)) {
            return ApiResponse.notFound("Utilisateur non trouvé");
        }
        
        Map<String, Object> user = users.get(id);
        if (name != null) user.put("name", name);
        if (email != null) user.put("email", email);
        
        return ApiResponse.success(user);
    }
    
    // Supprimer un utilisateur
    @DELETE
    @Path("/{id}")
    public ApiResponse deleteUser(@PathParam("id") int id) {
        if (!users.containsKey(id)) {
            return ApiResponse.notFound("Utilisateur non trouvé");
        }
        
        users.remove(id);
        return ApiResponse.success(Collections.singletonMap("message", "Utilisateur supprimé avec succès"));
    }
    
    // Méthodes utilitaires
    private Map<String, Object> createUserMap(String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("createdAt", new Date());
        return user;
    }
    
    private int addUser(Map<String, Object> user) {
        int id = ++lastId;
        users.put(id, user);
        user.put("id", id);
        return id;
    }
    
    // Exemple d'une méthode qui retourne une vue au lieu de JSON
    @GET
    @Path("/list")
    @Produces(MediaType.TEXT_HTML)
    public ModelAndView listUsersView() {
        ModelAndView mv = new ModelAndView("/views/users.jsp");
        mv.setData(Collections.singletonMap("users", users));
        return mv;
    }
}
