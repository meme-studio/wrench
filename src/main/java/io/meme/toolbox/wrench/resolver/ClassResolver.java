package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.utils.ResourceCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    public Stream<InputStream> resolve(Path path, ResourceCollector collector) {
        return Stream.of(path)
                     .map(Path::toFile)
                     .map(unchecked(this::getFileInputStream).apply(collector));
    }

    private FileInputStream getFileInputStream(ResourceCollector collector, File file) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        collector.collect(fileInputStream);
        return fileInputStream;
    }

    @Override
    public boolean isTypeMatched(Path path) {
        return path.toString().endsWith(".class");
    }
}
