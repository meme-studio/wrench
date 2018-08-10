package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.MethodResolver;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.NameUtils;
import io.meme.toolbox.wrench.utils.Predicates;
import io.vavr.Function2;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * @author meme
 * @since 2018/7/23
 */
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MethodMessage extends MethodResolver implements Serializable {
    private static final long serialVersionUID = 1286151805906509943L;
    private final String className;
    private final String name;
    @EqualsAndHashCode.Include
    private final String desc;
    private final int access;
    @Getter
    private final List<ArgumentMessage> argumentMessages;

    public boolean isAbstract() {
        return AccessUtils.isAbstract(access);
    }

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    public boolean isConstructor() {
        return Objects.equals("<init>", name);
    }

    public String getMethodDescription() {
        return argumentMessages.stream()
                               .map(argument -> String.format("%s %s", argument.getLongTypeName(), argument.getArgumentName()))
                               .collect(joining(", ", String.format("%s(", getMethodPrefix()), ")"));
    }

    private String getMethodPrefix() {
        String[] prefixes =
                isConstructor()
                        ?
                        new String[]{
                                AccessUtils.getAccessType(access),
                                NameUtils.calcSimpleClassName(className)
                        }
                        :
                        new String[]{
                                AccessUtils.getAccessType(access),
                                AccessUtils.getStaticOrAbstract(access),
                                AccessUtils.getSynchronized(access),
                                getReturnType(),
                                name
                        };
        return Stream.of(prefixes)
                     .filter(Predicate.isEqual("").negate())
                     .collect(joining(" "));
    }

    public String getReturnType() {
        return Type.getReturnType(desc).getClassName();
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        argumentMessages.stream()
                        .filter(Predicates.of(Function2.of(Objects::equals)
                                                       .apply(index)
                                                       .compose(ArgumentMessage::getLvtSlotIndex)))
                        .findAny()
                        .ifPresent(argumentMessage -> argumentMessage.setArgumentName(name));
    }

}
