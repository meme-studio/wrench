package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Asserts {
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
