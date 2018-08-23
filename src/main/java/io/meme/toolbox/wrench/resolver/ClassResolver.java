package io.meme.toolbox.wrench.resolver;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.NameUtils;
import io.vavr.API;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.negate;
import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.Function;
import static io.vavr.API.unchecked;

/**
 * @author meme
 * @since 1.0
 */
public class ClassResolver implements ClassTypeResolver {

    @Override
    public Stream<ClassMessage> resolve(String path, Configuration configuration) {
        return Stream.of(path)
                     .filter(filter(configuration))
                     .map(API.<String, File>unchecked(File::new))
                     .map(unchecked(FileInputStream::new))
                     .map(Function($::determineClassMessage).apply(configuration));
    }

    private Predicate<String> filter(Configuration configuration) {
        return negate($::isAnonymousClass).and(predicate(Function(configuration::isPackageIncluded).compose(NameUtils::calcInternalName)))
                                          .and(negate(Function(configuration::isPackageExcluded).compose(NameUtils::calcInternalName)));
    }

    @Override
    public boolean isTypeMatched(String path) {
        return path.endsWith(".class");
    }
}
