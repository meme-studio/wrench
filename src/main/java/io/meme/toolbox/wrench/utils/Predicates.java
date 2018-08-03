package io.meme.toolbox.wrench.utils;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author meme
 * @since 2018/8/3
 */
public class Predicates {

    public static <T> Predicate<T> of(Predicate<T> predicate) {
        return Objects.requireNonNull(predicate);
    }

    public static <T> Predicate<T> negate(Predicate<T> predicate) {
        return Objects.requireNonNull(predicate).negate();
    }

    public static <T> Predicate<T> of(Function<T, Boolean> function) {
        Objects.requireNonNull(function);
        return function::apply;
    }

}
