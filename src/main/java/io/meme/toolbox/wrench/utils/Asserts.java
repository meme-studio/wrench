package io.meme.toolbox.wrench.utils;

/**
 * @author meme
 * @since 2018/8/6
 */
public class Asserts {
    public static <T> T illegal(String type) {
        throw new IllegalArgumentException(type);
    }
}
