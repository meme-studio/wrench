package io.meme.toolbox.wrench.resolver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import static io.vavr.API.unchecked;

/**
 * @author meme
 * @since 1.0
 */
public class ClassResolver implements ClassFileResolver {

    @Override
    public Stream<InputStream> resolve(Path path) {
        return Stream.of(path)
                     .map(Path::toFile)
                     .map(unchecked(FileInputStream::new));
    }

    @Override
    public boolean isTypeMatched(Path path) {
        return path.toString().endsWith(".class");
    }
}
