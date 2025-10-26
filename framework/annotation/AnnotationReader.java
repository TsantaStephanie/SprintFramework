package framework.annotation;

import java.lang.reflect.Method;
import java.io.IOException;
import java.util.Set;

import framework.controller.Controller;

public class AnnotationReader {
    
    public static void readGetMappingAnnotations(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping annotation = method.getAnnotation(GetMapping.class);
                String url = annotation.value();
                System.out.println("URL trouvée: " + url);
            }
        }
    }

    public static Set<Class<?>> findControllers(String basePackage) throws IOException {
        return ClassScanner.findClassesWithAnnotation(basePackage, Controller.class);
    }
}
