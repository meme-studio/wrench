package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * @author meme
 * @since 2018/7/27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NameUtils {

    public static String calcSimpleClassName(String longClassName) {
        return Objects.requireNonNull(longClassName)
                      .substring(longClassName.lastIndexOf(".") + 1);
    }

    public static String calcPackageName(String longClassName) {
        return Objects.requireNonNull(longClassName)
                      .substring(0, longClassName.lastIndexOf("."));
    }

    //FIXME
    public static String calcInternalName(String name) {
        return Objects.requireNonNull(name)
                      .replace('/', '.')
                      .replace('\\', '.')
                      .replaceAll("(\\${2})+", Matcher.quoteReplacement("$."));
    }

    public static String[] calcInternalNames(List<String> names) {
        return calcInternalNames(names.toArray(new String[0]));
    }

    public static String[] calcInternalNames(String... names) {
        return Arrays.stream(names)
                     .map(NameUtils::calcInternalName)
                     .toArray(String[]::new);
    }

}
