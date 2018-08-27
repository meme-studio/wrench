package io.meme.toolbox.wrench.test;

import io.meme.toolbox.wrench.Wrench;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Properties;

class AnyTest {

    @Test
    void any() throws URISyntaxException, IOException {
        Properties properties = new Properties();
        properties.load(Wrench.class.getResourceAsStream("wrench.properties"));
        Arrays.stream(properties.getProperty("wrench.resolvers").split(",")).forEach(System.out::println);
    }
}