package io.meme.toolbox.wrench.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

class AnyTest {

    @Test
    void any() throws URISyntaxException, IOException {
        InputStream inputStream = new URL("https://repo.maven.apache.org/maven2/asm/asm/3.3.1/asm-3.3.1.jar").openStream();
        JarInputStream jarInputStream = new JarInputStream(inputStream);
        String name = jarInputStream.getNextJarEntry().getName();
        JarEntry nextJarEntry = jarInputStream.getNextJarEntry();
        System.out.println(name);
    }
}