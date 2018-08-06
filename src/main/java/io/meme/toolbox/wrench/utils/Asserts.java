package io.meme.toolbox.wrench.utils;

/**
 * @author meme
 * @since 2018/8/6
 */
public class Asserts {
    public static <T> T fail(String type) {
        throw new IllegalArgumentException(type);
    }

    public static <T> T success(T any) {
        return any;
    }

    public static boolean requireTrue(boolean result, String errorMessage) {
        return result ? success(result) : fail(errorMessage);
    }
}
