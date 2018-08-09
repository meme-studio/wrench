package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.ClassResolver;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.NameUtils;
import io.vavr.Function3;
import io.vavr.Predicates;
import io.vavr.control.Option;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author meme
 * @since 2018/7/23
 */
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ClassMessage extends ClassResolver implements Serializable {
    private static final long serialVersionUID = -5621028783726663753L;

    @EqualsAndHashCode.Include
    @Getter
    private String name;
    private int access;
    private String signature;
    @Getter
    private String superClassName;
    @Getter
    private List<String> interfaceNames;
    @Getter
    private List<MethodMessage> methodMessages = new ArrayList<>();
    @Getter
    private List<FieldMessage> fieldMessages = new ArrayList<>();

    private final int ignoreVisibilities;

    public List<String> listSuperClassAndInterfaceNames() {
        return Stream.concat(interfaceNames.stream(), Stream.of(superClassName))
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());
    }

    public String getSimpleName() {
        return NameUtils.calcSimpleClassName(name);
    }

    public String getPackageName() {
        return NameUtils.calcPackageName(name);
    }

    public boolean isAbstract() {
        return AccessUtils.isAbstract(access);
    }

    public boolean isInterface() {
        return AccessUtils.isInterface(access);
    }

    public boolean isEnum() {
        return AccessUtils.isEnum(access);
    }

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = NameUtils.calcInternalName(name);
        this.signature = signature;
        this.access = access;
        this.superClassName = superName;
        this.interfaceNames = Arrays.asList(interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return visitAndReturn(access, name, desc, this::calcFieldMessage);
    }

    private FieldMessage calcFieldMessage(String name, String desc, int access) {
        FieldMessage field = FieldMessage.of(name, desc, access);
        fieldMessages.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return visitAndReturn(access, name, desc, this::calcMethodMessage);
    }

    private <T> T visitAndReturn(int access, String name, String desc, Function3<String, String, Integer, T> messageGenerator) {
        return Option.of(access)
                     .filter(Predicates.anyOf(ignore -> $.isIgnoreFieldVisibility(ignoreVisibilities), AccessUtils::isPublic))
                     .map(messageGenerator.apply(name, desc))
                     .getOrNull();
    }

    private MethodMessage calcMethodMessage(String name, String desc, int access) {
        List<ArgumentMessage> argumentMessages = computeLvtSlotIndices(AccessUtils.isStatic(access), Type.getArgumentTypes(desc));
        MethodMessage method = MethodMessage.of(name, desc, access, argumentMessages);
        methodMessages.add(method);
        return method;
    }

    private static List<ArgumentMessage> computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
        return IntStream.range(0, paramTypes.length)
                        .boxed()
                        .map(Function3.of(ClassMessage::calcArgumentMessage)
                                      .apply(isStatic ? 0 : 1, paramTypes))
                        .collect(Collectors.toList());
    }

    private static ArgumentMessage calcArgumentMessage(int index, Type[] paramTypes, int i) {
        return ArgumentMessage.of(paramTypes[i].getClassName(), String.format("arg%d", i), calcIndex(index, paramTypes, i));
    }

    private static int calcIndex(int index, Type[] paramTypes, int i) {
        return Option.of(i)
                     .filter(Predicate.isEqual(0))
                     .map(ignore -> index)
                     .getOrElse(() -> calcPreIndex(index, paramTypes, i - 1));
    }

    private static int calcPreIndex(int index, Type[] paramTypes, int i) {
        int preIndex = calcIndex(index, paramTypes, i);
        return $.isWideType(paramTypes[i]) ? preIndex + 2 : preIndex + 1;
    }

}

