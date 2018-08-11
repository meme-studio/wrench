package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author meme
 * @since 2018/8/3
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PredicateEx {

    public static <T> Predicate<T> of(Predicate<T> predicate) {
        return Objects.requireNonNull(predicate);
    }

    public static <T> Predicate<T> of(Function<T, Boolean> function) {
        return Objects.requireNonNull(function)::apply;
    }

    public static <T> Predicate<T> negate(Function<T, Boolean> function) {
        return of(function).negate();
    }

}
