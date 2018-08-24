package io.meme.toolbox.wrench.resolver;

import io.vavr.API;
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
    public Stream<InputStream> resolve(Path path) {
        return Stream.of(path)
                     .map(API.<File, JarFile>unchecked(JarFile::new).compose(Path::toFile))
                     .collect(collectingAndThen(toMap(identity(), JarFile::stream), this::determineClassInputStreams));
    }

    private Stream<InputStream> determineClassInputStreams(Map<JarFile, Stream<JarEntry>> jars) {
        return jars.entrySet()
                   .parallelStream()
                   .flatMap(this::forEachEntry);
    }

    private Stream<InputStream> forEachEntry(Map.Entry<JarFile, Stream<JarEntry>> entry) {
        return entry.getValue()
                    .filter(predicate(Function(this::isClassFileType).compose(JarEntry::getName)))
                    .map(Function(this::getClassInputStream).apply(entry));
    }

    @SneakyThrows
    private InputStream getClassInputStream(Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        return entry.getKey().getInputStream(jarEntry);
    }

    private boolean isClassFileType(String path) {
        return path.endsWith(".class");
    }

    @Override
    public boolean isTypeMatched(Path path) {
        return Stream.of(".jar", ".war", ".ear").anyMatch(path.toString()::endsWith);
    }
}
