package framework.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    public static Set<Class<?>> findClassesWithAnnotation(String basePackage, Class<? extends Annotation> annotation) throws IOException {
        Set<Class<?>> result = new HashSet<>();
        String path = basePackage.replace('.', '/');
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassScanner.class.getClassLoader();
        }
        Enumeration<URL> resources = cl.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(resource.getFile(), java.nio.charset.StandardCharsets.UTF_8);
                findInDirectory(result, basePackage, new File(filePath), annotation, cl);
            } else if ("jar".equals(protocol)) {
                findInJar(result, resource, path, annotation, cl);
            }
        }
        return result;
    }

    private static void findInDirectory(Set<Class<?>> result, String basePackage, File directory,
                                        Class<? extends Annotation> annotation, ClassLoader cl) {
        if (!directory.exists()) return;
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                findInDirectory(result, basePackage + "." + file.getName(), file, annotation, cl);
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className, false, cl);
                    if (clazz.isAnnotationPresent(annotation)) {
                        result.add(clazz);
                    }
                } catch (Throwable ignored) {
                    // Ignore classes that cannot be loaded
                }
            }
        }
    }

    private static void findInJar(Set<Class<?>> result, URL resource, String path,
                                  Class<? extends Annotation> annotation, ClassLoader cl) throws IOException {
        JarURLConnection conn = (JarURLConnection) resource.openConnection();
        try (JarFile jarFile = conn.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                    String className = name.replace('/', '.').substring(0, name.length() - 6);
                    try {
                        Class<?> clazz = Class.forName(className, false, cl);
                        if (clazz.isAnnotationPresent(annotation)) {
                            result.add(clazz);
                        }
                    } catch (Throwable ignored) {
                        // Ignore classes that cannot be loaded
                    }
                }
            }
        }
    }
}
