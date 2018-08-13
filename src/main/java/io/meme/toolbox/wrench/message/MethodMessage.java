package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.MethodResolver;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.NameUtils;
import io.meme.toolbox.wrench.utils.PredicateEx;
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
    @Getter
    private final String className;
    @Getter
    private final String name;
    @EqualsAndHashCode.Include
    private final String desc;
    private final int access;
    @Getter
    private final List<ArgumentMessage> argumentMessages;

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

    public String getMethodDescription() {
        return isClinit() ? "static {}" : getNonStaticMethodDescription();
    }

    private String getNonStaticMethodDescription() {
        String argumentsDescription = getArgumentsDescription();
        return getMethodPrefix().concat(
                isVarargs() ? argumentsDescription.replaceAll("(\\[])(?!.*\\1)", "...") : argumentsDescription);
    }

    private String getArgumentsDescription() {
        return argumentMessages.stream()
                               .map(argument -> String.format("%s %s", argument.getTypeName(), argument.getArgumentName()))
                               .collect(joining(", ", "(", ")"));
    }

    private String getMethodPrefix() {
        return Stream.of(isConstructor() ? constructorPrefixes() : nonConstructorPrefixes())
                     .filter(Predicate.isEqual("").negate())
                     .collect(joining(" "));
    }

    private String[] nonConstructorPrefixes() {
        return new String[]{
                AccessUtils.getAccessType(access),
                AccessUtils.getStaticOrAbstract(access),
                AccessUtils.getSynchronized(access),
                AccessUtils.getFinal(access),
                getReturnType(),
                name
        };
    }

    private String[] constructorPrefixes() {
        return new String[]{
                AccessUtils.getAccessType(access),
                NameUtils.calcSimpleClassName(className)
        };
    }

    public String getReturnType() {
        return NameUtils.calcInternalName(Type.getReturnType(desc).getClassName());
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        argumentMessages.stream()
                        .filter(PredicateEx.of(Function2.of(Objects::equals)
                                                        .apply(index)
                                                        .compose(ArgumentMessage::getIndex)))
                        .findAny()
                        .ifPresent(argumentMessage -> argumentMessage.setArgumentName(name));
    }

}
