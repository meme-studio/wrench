package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import io.vavr.Predicates;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author meme
 * @since 2018/7/30
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class Result {

    private final Map<String, ClassMessage> classMessages;

    public Set<String> getNamesOfClassMessages() {
        return classMessages.keySet();
    }

    //TODO
    public Result byType(Class<?>... classes) {
        List<String> types = listClassNames(classes);
        List<ClassMessage> classMessages = byType(this.classMessages.values(), types);
        List<String> types1 = classMessages.stream()
                                           .map(ClassMessage::getName)
                                           .collect(Collectors.toList());
        List<ClassMessage> classMessages1 = byType(this.classMessages.values(), types1);
        return this;
    }

    private List<ClassMessage> byType(Collection<ClassMessage> classMessages, List<String> types) {
        return classMessages.stream()
                            .filter(classMessage -> isMatchTypes(types, classMessage))
                            .collect(toList());
    }

    private boolean isMatchTypes(List<String> classNames, ClassMessage classMessage) {
        return classNames.stream()
                         .allMatch(Predicates.isIn(classMessage.listSuperClassAndInterfaceNames().toArray()));
    }

    public List<String> listClassNames(Class<?>... className) {
        return Stream.of(className)
                     .map(Class::getName)
                     .collect(Collectors.toList());
    }


}
