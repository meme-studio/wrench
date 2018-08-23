package io.meme.toolbox.wrench.utils;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.Result;
import io.meme.toolbox.wrench.message.ClassMessage;
import jdk.internal.org.objectweb.asm.ClassReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collector;

import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.Function;
import static io.vavr.API.Try;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class $ {

    public static boolean isAnonymousClass(String path) {
        return path.matches("^.*[$]\\d+.*$");
    }

    public static ClassMessage determineClassMessage(Configuration configuration, InputStream is) {
        return Try(() -> new ClassReader(is)).toOption()
                                             .filter(predicate(Function($::matchLimited).apply(configuration)))
                                             .map(Function($::getClassMessage).apply(configuration))
                                             .getOrNull();
    }

    private static boolean matchLimited(Configuration configuration, ClassReader reader) {
        return configuration.isEnableVisibleClass() || AccessUtils.isPublic(reader.getAccess());
    }

    public static ClassMessage getClassMessage(Configuration configuration, ClassReader reader) {
        ClassMessage classMessage = ClassMessage.of(configuration);
        reader.accept(classMessage, ClassReader.SKIP_FRAMES);
        return classMessage;
    }

    public static boolean matchPackages(List<String> packages, String path) {
        return packages.stream().anyMatch(NameUtils.calcInternalName(path)::contains);
    }

    public static Collector<ClassMessage, ?, Result> toResult() {
        return collectingAndThen(toList(), Result::of);
    }



}
