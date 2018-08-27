package io.meme.toolbox.wrench.resolver.file;

import io.meme.toolbox.wrench.utils.ResourceCollector;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author meme
 * @since 1.0
 */
public interface FileResolver {

    Stream<InputStream> resolve(File file, ResourceCollector collector);

    List<String> supportsTypes();

}
