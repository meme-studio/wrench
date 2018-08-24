package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.utils.ResourceCollector;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author meme
 * @since 1.0
 */
public interface ClassFileResolver {

    Stream<InputStream> resolve(Path path, ResourceCollector collector);

    boolean isTypeMatched(Path path);

}
