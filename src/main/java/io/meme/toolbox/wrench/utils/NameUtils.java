package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author meme
 * @since 2018/7/27
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

    public static String[] calcInternalNames(@NonNull List<String> names) {
        return calcInternalNames(names.toArray(new String[0]));
    }

    public static String[] calcInternalNames(@NonNull String... names) {
        return Arrays.stream(names)
                     .map(NameUtils::calcInternalName)
                     .toArray(String[]::new);
    }

}
