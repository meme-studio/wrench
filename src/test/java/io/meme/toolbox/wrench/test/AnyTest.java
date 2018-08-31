package io.meme.toolbox.wrench.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.net.URLClassLoader;

class AnyTest {

    @Test
    @SneakyThrows
    void any() {


//        URI uri1 = new File("res").toURI();
//        URL url = new URL("jar:file:/data/spring-boot-theory.jar!/BOOT-INF/lib/spring-aop-5.0.4.RELEASE.jar!/org/springframework/aop/SpringProxy.class");
//        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
//        URI uri = url.toURI();
//
//        JarFile jarFile = urlConnection.getJarFile();

//        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL("https://repo.maven.apache.org/maven2/asm/asm/3.3.1/asm-3.3.1.jar")});
//        //Class<?> aClass = urlClassLoader.loadClass("org.objectweb.asm.AnnotationVisitor.class");
//        URL[] urLs = urlClassLoader.getURLs();
//        Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator)).forEach(System.out::println);
//        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//        sun.misc.Launcher.getBootstrapClassPath().getURLs();
//        Method method = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
//        method.setAccessible(true);
//        Object invoke = method.invoke(systemClassLoader, "");
//        System.out.println(System.getProperty("sun.boot.class.path"));
//        System.out.println();
//        System.out.println(System.getProperty("java.ext.dirs"));
//        System.out.println();
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        for (URL url : urls) {
            System.out.println(url.toExternalForm());
        }
        System.out.println();
        for (URL url : systemClassLoader.getURLs()) {
            System.out.println(url.toExternalForm());
        }
        System.out.println();

        URLClassLoader parent =(URLClassLoader)  Thread.currentThread().getContextClassLoader().getParent();

        for (URL url : parent.getURLs()) {
            System.out.println(url.toExternalForm());
        }
//        Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator)).forEach(System.out::println);
        System.out.println();


        }
}