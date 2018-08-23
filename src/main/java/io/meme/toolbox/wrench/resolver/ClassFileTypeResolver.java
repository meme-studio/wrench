package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.message.ClassMessage;

import java.util.stream.Stream;

/**
 * @author meme
 * @since 1.0
 */
public interface ClassFileTypeResolver {

   Stream<ClassMessage> scan(String path, Configuration configuration);

    boolean isTypeMatch(String path);


}
