package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.utils.ResourceCollector;
import io.vavr.API;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
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
                     .map(API.<File, JarFile>unchecked(this::getJarFile).compose(Path::toFile))
                     .collect(collectingAndThen(toMap(identity(), JarFile::stream), this::determineClassInputStreams));
    }

    private JarFile getJarFile(File file) throws IOException {
        JarFile jarFile = new JarFile(file);
        ResourceCollector.collect(jarFile);
        return jarFile;
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
        InputStream inputStream = entry.getKey().getInputStream(jarEntry);
        //上面代码抛异常时，可能存在内存泄漏
        ResourceCollector.collect(inputStream);
        return inputStream;
    }

    private boolean isClassFileType(String path) {
        return path.endsWith(".class");
    }

    @Override
    public boolean isTypeMatched(Path path) {
        return Stream.of(".jar", ".war", ".ear").anyMatch(path.toString()::endsWith);
    }
}
