package io.meme.toolbox.wrench.resolver.file;

import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.ResourceCollector;
import io.vavr.API;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

/**
 * @author meme
 * @since 1.0
 */
public class JarResolver implements FileResolver {

    @Override
    public Stream<InputStream> resolve(File file, ResourceCollector collector) {
        return Stream.of(file)
                     .map(API.<File, JarFile>unchecked(JarFile::new))
                     .peek(collector::collect)
                     .collect(collectingAndThen(toMap(identity(), JarFile::stream), Function(this::determineClassInputStreams).apply(collector)));
    }

    @Override
    public List<String> supportsTypes() {
        return Arrays.asList(".jar", ".war", ".ear");
    }

    private Stream<InputStream> determineClassInputStreams(ResourceCollector collector, Map<JarFile, Stream<JarEntry>> jars) {
        return jars.entrySet()
                   .parallelStream()
                   .flatMap(Function(this::forEachEntry).apply(collector));
    }

    private Stream<InputStream> forEachEntry(ResourceCollector collector, Map.Entry<JarFile, Stream<JarEntry>> entry) {
        return entry.getValue()
                    .filter(predicate(Function($::isClassFileType).compose(JarEntry::getName)))
                    .map(Function(this::getClassInputStream).apply(collector, entry));
    }

    private InputStream getClassInputStream(ResourceCollector collector, Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        return Option(entry).map(unchecked(JarFile::getInputStream).reversed()
                                                                   .apply(jarEntry)
                                                                   .compose(Map.Entry::getKey))
                            .peek(collector::collect)
                            .getOrNull();
    }

}
