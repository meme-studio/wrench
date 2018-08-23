package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.message.ClassMessage;

import java.util.stream.Stream;

/**
 * @author meme
 * @since 1.0
 */
public interface ClassTypeResolver {

    Stream<ClassMessage> resolve(String path, Configuration configuration);

    boolean isTypeMatched(String path);

}
