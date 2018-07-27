package io.meme.joke.classscanner.utils;

/**
 * @author meme
 * @since 2018/7/27
 */
public final class NameUtils {

    public static String calcSimpleClassName(String longClassName) {
        return longClassName.substring(longClassName.lastIndexOf(".") + 1);
    }

    public static String calcPackageName(String longClassName) {
        return longClassName.substring(0, longClassName.lastIndexOf("."));
    }

    public static String calcInternalName(String name) {
        return name.replace('.', '/');
    }
}
