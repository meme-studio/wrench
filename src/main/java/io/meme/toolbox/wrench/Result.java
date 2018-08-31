package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassInfo;
import io.meme.toolbox.wrench.utils.$;
import io.vavr.Function2;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.*;
import static io.vavr.Predicates.*;
import static java.util.stream.Collectors.*;


/**
 * @author meme
 * @since 1.0
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class Result {

    private final List<ClassInfo> classMessages;

    public Set<String> listClassNames() {
        return classMessages.stream()
                            .map(ClassInfo::getName)
                            .collect(Collectors.toSet());
    }

    public Result byTypes(Class<?>... classes) {
        return byTypes(listClassNames(classes)).stream()
                                                 .collect($.toResult());
    }

    public Result byNames(Class<?>... classes) {
        return by(listClassNames(classes).toArray(), ClassInfo::getName);
    }

    public Result byPackages(String... packageNames) {
        return by(packageNames, ClassInfo::getPackageName);
    }

    public Map<String, List<ClassInfo>> groupingByPackageName() {
        return classMessages.stream()
                            .collect(groupingBy(ClassInfo::getPackageName));
    }

    private <T> Result by(T[] conditions, Function<ClassInfo, T> function) {
        return classMessages.stream()
                            .filter(predicate(function(isIn(conditions)).compose(function)))
                            .collect($.toResult());
    }

    private List<ClassInfo> byTypes(List<String> types) {
        return classMessages.stream()
                            .filter(predicate(Function2.of(this::isMatchTypes).apply(types)))
                            .collect(collectingAndThen(toList(), Function2.<List<String>, List<ClassInfo>, List<ClassInfo>>of(this::byTypes).apply(types)));
    }

    private List<ClassInfo> byTypes(List<String> types, List<ClassInfo> classMessages) {
        return Objects.equals(classMessages.size(), types.size()) ? classMessages : byTypes(listClassNames(classMessages));
    }

    private List<String> listClassNames(List<ClassInfo> classMessages) {
        return classMessages.stream()
                            .map(ClassInfo::getName)
                            .collect(toList());
    }

    private boolean isMatchTypes(List<String> classNames, ClassInfo classMessage) {
        return Stream.concat(classMessage.getInterfaceNames().stream(), Stream.of(classMessage.getSuperClassName(), classMessage.getName()))
                     .anyMatch(isIn(classNames.toArray()));
    }

    private List<String> listClassNames(Class<?>... className) {
        return Stream.of(className)
                     .map(Class::getName)
                     .collect(toList());
    }



}
