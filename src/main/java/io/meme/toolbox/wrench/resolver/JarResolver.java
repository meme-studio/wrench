package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.utils.ResourceCollector;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.Function;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

/**
 * @author meme
 * @since 1.0
 */
public class JarResolver implements ClassFileResolver {

    @Override
    public Stream<InputStream> resolve(Path path, ResourceCollector collector) {
        return Stream.of(path)
                     .map(Function(this::getJarFile).apply(collector).compose(Path::toFile))
                     .collect(collectingAndThen(toMap(identity(), JarFile::stream), Function(this::determineClassInputStreams).apply(collector)));
    }

    @SneakyThrows
    private JarFile getJarFile(ResourceCollector collector, File file) {
        JarFile jarFile = new JarFile(file);
        collector.collect(jarFile);
        return jarFile;
    }

    private Stream<InputStream> determineClassInputStreams(ResourceCollector collector, Map<JarFile, Stream<JarEntry>> jars) {
        return jars.entrySet()
                   .parallelStream()
                   .flatMap(Function(this::forEachEntry).apply(collector));
    }

    private Stream<InputStream> forEachEntry(ResourceCollector collector, Map.Entry<JarFile, Stream<JarEntry>> entry) {
        return entry.getValue()
                    .filter(predicate(Function(this::isClassFileType).compose(JarEntry::getName)))
                    .map(Function(this::getClassInputStream).apply(collector, entry));
    }

    @SneakyThrows
    private InputStream getClassInputStream(ResourceCollector collector, Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        InputStream inputStream = entry.getKey().getInputStream(jarEntry);
        collector.collect(inputStream);
        return inputStream;
    }

    private boolean isClassFileType(String path) {
        return path.endsWith(".class");
    }

    @Override
    public boolean isTypeMatched(Path path) {
        return Stream.of(".jar").anyMatch(path.toString()::endsWith);
    }
}
