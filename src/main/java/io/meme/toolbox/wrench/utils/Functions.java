package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Functions {

    public static <T> Predicate<T> predicate(@NonNull Function<T, Boolean> function) {
        return function::apply;
    }

    public static <T> Predicate<T> negate(Function<T, Boolean> function) {
        return predicate(function).negate();
    }

    public static <T> Function<T, Boolean> function(@NonNull Predicate<T> predicate) {
        return predicate::test;
    }

}
