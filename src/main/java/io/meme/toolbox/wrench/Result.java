package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.PredicateEx;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Predicates;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * @author meme
 * @since 2018/7/30
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class Result {

    private final List<ClassMessage> classMessages;

    public Set<String> listClassNames() {
        return classMessages.stream()
                            .map(ClassMessage::getName)
                            .collect(Collectors.toSet());
    }

    public Result byTypes(Class<?>... classes) {
        return byTypes($.listClassNames(classes)).stream()
                                                 .collect(collectingAndThen(toList(), Result::of));
    }

    public Result byNames(Class<?>... classes) {
        return by($.listClassNames(classes).toArray(), ClassMessage::getName);
    }

    public Result byPackages(String... packageNames) {
        return by(packageNames, ClassMessage::getPackageName);
    }

    public Map<String, List<ClassMessage>> groupingByPackageName() {
        return classMessages.stream()
                            .collect(groupingBy(ClassMessage::getPackageName));
    }

    private <T> Result by(T[] conditions, Function<ClassMessage, T> function) {
        return classMessages.stream()
                            .filter(PredicateEx.of(PredicateEx.toFunction(Predicates.isIn(conditions))
                                                              .compose(function)))
                            .collect($.toResult());
    }

    private List<ClassMessage> byTypes(List<String> types) {
        return classMessages.stream()
                            .filter(PredicateEx.of(Function2.of(this::isMatchTypes).apply(types)))
                            .collect(collectingAndThen(Collectors.toList(), Function2.<List<String>, List<ClassMessage>, List<ClassMessage>>of(this::byTypes).apply(types)));
    }

    private List<ClassMessage> byTypes(List<String> types, List<ClassMessage> classMessages) {
        return Objects.equals(classMessages.size(), types.size()) ? classMessages : byTypes(listClassNames(classMessages));
    }

    private List<String> listClassNames(List<ClassMessage> classMessages) {
        return classMessages.stream()
                            .map(ClassMessage::getName)
                            .collect(Collectors.toList());
    }

    private boolean isMatchTypes(List<String> classNames, ClassMessage classMessage) {
        return Stream.concat(classMessage.getInterfaceNames().stream(), Stream.of(classMessage.getSuperClassName(), classMessage.getName()))
                     .anyMatch(Predicates.isIn(classNames.toArray()));
    }


}
