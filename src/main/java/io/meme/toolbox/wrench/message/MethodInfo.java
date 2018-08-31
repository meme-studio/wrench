package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.visitor.Asm5MethodVisitor;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.Functions;
import io.meme.toolbox.wrench.utils.NameUtils;
import io.vavr.Function2;
import jdk.internal.org.objectweb.asm.Label;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * @author meme
 * @since 1.0
 */
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MethodInfo extends Asm5MethodVisitor implements Serializable {
    private static final long serialVersionUID = 1286151805906509943L;
    @Getter
    private final String className;
    @Getter
    private final String name;
    @Getter
    private final String returnType;
    private final int access;
    @Getter
    private final List<ArgumentInfo> argumentInfos;

    public boolean isFinal() {
        return AccessUtils.isFinal(access);
    }

    public boolean isAbstract() {
        return AccessUtils.isAbstract(access);
    }

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    public boolean isConstructor() {
        return Objects.equals("<init>", name);
    }

    public boolean isClinit() {
        return Objects.equals("<clinit>", name);
    }

    public boolean isVarargs() {
        return AccessUtils.isVarargs(access);
    }

    public boolean isSynchronized() {
        return AccessUtils.isSynchronized(access);
    }

    @Override
    public String toString() {
        return isClinit() ? "static {}" : getNonStaticMethodDescription();
    }

    private String getNonStaticMethodDescription() {
        String description = getArgumentsDescription();
        return getMethodPrefix().concat(isVarargs() ? description.replaceAll("(\\[])(?!.*\\1)", "...") : description);
    }

    private String getArgumentsDescription() {
        return argumentInfos.stream()
                            .map(argument -> String.format("%s %s", argument.getTypeName(), argument.getArgumentName()))
                            .collect(joining(", ", "(", ")"));
    }

    private String getMethodPrefix() {
        return Stream.of(isConstructor() ? constructorPrefixes() : nonConstructorPrefixes())
                     .filter(Predicate.isEqual("").negate())
                     .collect(joining(" "));
    }

    private String[] nonConstructorPrefixes() {
        return new String[]{Modifier.toString(access), returnType, name};
    }

    private String[] constructorPrefixes() {
        return new String[]{Modifier.toString(access), NameUtils.calcSimpleClassName(className)};
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        argumentInfos.stream()
                     .filter(Functions.predicate(Function2.of(Objects::equals)
                                                             .apply(index)
                                                             .compose(ArgumentInfo::getIndex)))
                     .findAny()
                     .ifPresent(argumentMessage -> argumentMessage.setArgumentName(name));
    }

}
