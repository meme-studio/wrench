package io.meme.joke.classscanner.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author meme
 * @since 2018/7/27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NameUtils {

    public static String calcSimpleClassName(String longClassName) {
        return longClassName.substring(longClassName.lastIndexOf(".") + 1);
    }

    public static String calcPackageName(String longClassName) {
        return longClassName.substring(0, longClassName.lastIndexOf("."));
    }

    public static String calcInternalName(String name) {
        return name.replace('/', '.');
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
