package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.regex.Matcher;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NameUtils {

    public static String calcSimpleClassName(@NonNull String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }

    public static String calcPackageName(@NonNull String className) {
        return className.substring(0, className.lastIndexOf("."));
    }

    //FIXME
    public static String calcInternalName(@NonNull String name) {
        return name.replace('/', '.')
                   .replace('\\', '.')
                   .replaceAll("(\\${2})+", Matcher.quoteReplacement("$."));
    }

    public static String[] calcInternalNames(@NonNull String... names) {
        return Arrays.stream(names)
                     .map(NameUtils::calcInternalName)
                     .toArray(String[]::new);
    }

}
