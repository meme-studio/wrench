package io.meme.toolbox.wrench.utils;

import io.meme.toolbox.wrench.Wrench;
import io.meme.toolbox.wrench.classpath.ClassPathProvider;
import io.meme.toolbox.wrench.resolver.file.FileResolver;
import io.vavr.API;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static io.meme.toolbox.wrench.utils.Functions.cast;
import static io.vavr.API.unchecked;

/**
 * @author meme
 * @since 1.0
 */
public final class Presets {

    public static List<FileResolver> defaultFileResolvers() {
        return getDefaultComponents("wrench.resolvers", FileResolver.class);
    }

    public static List<ClassPathProvider> defaultClassPathProviders() {
        return getDefaultComponents("wrench.providers", ClassPathProvider.class);
    }

    private static <T> List<T> getDefaultComponents(String componentName, Class<T> componentType) {
        return Arrays.stream(getProperty(componentName).split(","))
                     .map(API.<String, Class>unchecked(Class::forName)
                             .andThen(unchecked(Class::newInstance))
                             .andThen(cast(componentType)))
                     .collect(Collectors.toList());
    }

    @SneakyThrows
    private static String getProperty(String key) {
        Properties properties = new Properties();
        properties.load(Wrench.class.getResourceAsStream("wrench.properties"));
        return properties.getProperty(key);
    }


}
