package io.meme.toolbox.wrench.resolver.file;

import io.meme.toolbox.wrench.utils.ResourceCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static io.vavr.API.unchecked;
import static java.util.function.Function.identity;

/**
 * @author meme
 * @since 1.0
 */
public class ClassResolver implements FileResolver {

    @Override
    public Stream<InputStream> resolve(File file, ResourceCollector collector) {
        return Stream.of(file)
                     .map(unchecked(FileInputStream::new))
                     .peek(collector::collect)
                     .map(identity());
    }

    @Override
    public List<String> supportsTypes() {
        return Collections.singletonList(".class");
    }

}
