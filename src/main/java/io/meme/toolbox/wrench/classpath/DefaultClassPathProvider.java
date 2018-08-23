package io.meme.toolbox.wrench.classpath;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author meme
 * @since 1.0
 */
public class DefaultClassPathProvider implements ClassPathProvider {
    @Override
    public List<String> listClassPaths() {
        return Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparator));
    }
}
