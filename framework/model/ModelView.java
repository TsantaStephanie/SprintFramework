package framework.model;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String view; 
    private Map<String, Object> data; 

    public ModelView() {
        this.data = new HashMap<>();
    }

    public ModelView(String view) {
        this();
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}