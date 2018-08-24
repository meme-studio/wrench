package io.meme.toolbox.wrench.resolver;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * @author meme
 * @since 1.0
 */
public interface ClassFileResolver {

    Stream<InputStream> resolve(Path path);

    boolean isTypeMatched(Path path);

}
