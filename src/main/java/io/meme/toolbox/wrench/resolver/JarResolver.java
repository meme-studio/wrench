package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.NameUtils;
import io.vavr.API;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.negate;
import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.Function;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

/**
 * @author meme
 * @since 1.0
 */
public class JarResolver implements ClassTypeResolver {

    @Override
    public Stream<ClassMessage> resolve(String path, Configuration configuration) {
        return Stream.of(path)
                     .map(API.<String, JarFile>unchecked(JarFile::new))
                     .collect(collectingAndThen(toMap(identity(), JarFile::stream), Function(this::determineClassMessages).apply(configuration)));
    }

    private Stream<ClassMessage> determineClassMessages(Configuration configuration, Map<JarFile, Stream<JarEntry>> jars) {
        return jars.entrySet()
                   .parallelStream()
                   .flatMap(Function(this::forEachEntry).apply(configuration));
    }

    private Stream<ClassMessage> forEachEntry(Configuration configuration, Map.Entry<JarFile, Stream<JarEntry>> entry) {
        return entry.getValue()
                    .filter(predicate(Function(this::isClassFileType).compose(JarEntry::getName))
                            .and(negate(Function($::isAnonymousClass).compose(JarEntry::getName)))
                            .and(predicate(Function(configuration::isPackageIncluded).compose(NameUtils::calcInternalName).compose(JarEntry::getName)))
                            .and(negate(Function(configuration::isPackageExcluded).compose(NameUtils::calcInternalName).compose(JarEntry::getName))))
                    .map(Function(this::getClassInputStream).apply(entry))
                    .map(Function($::determineClassMessage).apply(configuration));
    }

    @SneakyThrows
    private InputStream getClassInputStream(Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        return entry.getKey().getInputStream(jarEntry);
    }

    private boolean isClassFileType(String path) {
        return path.endsWith(".class");
    }

    @Override
    public boolean isTypeMatched(String path) {
        return Stream.of("jar", "war", "ear").anyMatch(path::endsWith);
    }
}
