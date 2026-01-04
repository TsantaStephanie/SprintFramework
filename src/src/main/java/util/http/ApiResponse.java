package util.http;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    private String status;
    private int code;
    private Object data;
    private Map<String, Object> metadata;

    public ApiResponse(String status, int code, Object data) {
        this.status = status;
        this.code = code;
        this.data = data;
        this.metadata = new HashMap<>();
    }

    // Getters et setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    // Méthodes utilitaires pour créer des réponses courantes
    public static ApiResponse success(Object data) {
        return new ApiResponse("success", 200, data);
    }

    public static ApiResponse created(Object data) {
        return new ApiResponse("success", 201, data);
    }

    public static ApiResponse error(String message, int code) {
        return new ApiResponse("error", code, message);
    }

    public static ApiResponse notFound(String message) {
        return error(message, 404);
    }

    public static ApiResponse badRequest(String message) {
        return error(message, 400);
    }

    public static ApiResponse serverError(String message) {
        return error(message, 500);
    }
}
