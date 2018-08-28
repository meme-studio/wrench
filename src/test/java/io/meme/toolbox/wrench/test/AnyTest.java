package io.meme.toolbox.wrench.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;

class AnyTest {

    @Test
    @SneakyThrows
    void any() {
        URI uri1 = new File("res").toURI();
        URL url = new URL("jar:file:/data/spring-boot-theory.jar!/BOOT-INF/lib/spring-aop-5.0.4.RELEASE.jar!/org/springframework/aop/SpringProxy.class");
        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
        URI uri = url.toURI();

        JarFile jarFile = urlConnection.getJarFile();
        System.out.println();
    }
}