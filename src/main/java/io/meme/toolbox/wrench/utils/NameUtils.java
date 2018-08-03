package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * @author meme
 * @since 2018/7/27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NameUtils {

    public static String calcSimpleClassName(String longClassName) {
        return Objects.requireNonNull(longClassName).substring(longClassName.lastIndexOf(".") + 1);
    }

    public static String calcPackageName(String longClassName) {
        return Objects.requireNonNull(longClassName).substring(0, longClassName.lastIndexOf("."));
    }

    public static String calcInternalName(String name) {
        return Objects.requireNonNull(name)
                      .replace('/', '.')
                      .replace('$', '.');
    }

    //TODO
    public static List<String> calcInterfaceNames(String signature) {
        return null;
    }

    //TODO
    public static String calcSuperClassName(String signature) {
        return null;
    }
}
