package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.ClassResolver;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.NameUtils;
import io.vavr.Function3;
import io.vavr.Predicates;
import io.vavr.control.Option;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import lombok.*;
import lombok.experimental.ExtensionMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author meme
 * @since 2018/7/23
 */
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ExtensionMethod({AccessUtils.class, Arrays.class})
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

    //FIXME for array type
    public static ClassMessage of(Class<?> clazz) {
        return of(clazz.getTypeName());
    }

    @SneakyThrows
    public static ClassMessage of(String className) {
        return $.getClassMessage($.INCLUDE_ALL_INVISIBLE, new ClassReader(className));
    }

    public boolean isFinal() {
        return AccessUtils.isFinal(access);
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
        this.superClassName = Option.of(superName)
                                    .filter(Objects::nonNull)
                                    .map(NameUtils::calcInternalName)
                                    .getOrNull();
        this.interfaceNames = Arrays.asList(NameUtils.calcInternalNames(interfaces));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return visitAndReturn(access, name, desc, this::calcFieldMessage, ignore -> $.isFieldVisibilityIgnored(ignoreVisibilities));
    }

    private FieldMessage calcFieldMessage(String name, String desc, int access) {
        FieldMessage field = FieldMessage.of(name, NameUtils.calcInternalName(Type.getType(desc).getClassName()), access);
        fieldMessages.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return visitAndReturn(access, name, desc, this::calcMethodMessage, ignore -> $.isMethodVisibilityIgnored(ignoreVisibilities));
    }

    private <T> T visitAndReturn(int access, String name, String desc, Function3<String, String, Integer, T> messageGenerator, Predicate<Integer> isAccessIgnore) {
        return Option.of(access)
                     .filter(Predicates.anyOf(isAccessIgnore, AccessUtils::isPublic)
                                       .and(Predicates.noneOf(AccessUtils::isSynthetic)))
                     .map(messageGenerator.apply(name, desc))
                     .getOrNull();
    }

    private MethodMessage calcMethodMessage(String name, String desc, int access) {
        List<ArgumentMessage> argumentMessages = calcArgumentMessages(AccessUtils.isStatic(access), Type.getArgumentTypes(desc));
        MethodMessage method = MethodMessage.of(getName(), name, NameUtils.calcInternalName(Type.getReturnType(desc).getClassName()), access, argumentMessages);
        methodMessages.add(method);
        return method;
    }

    /**
     * Reference to Spring org.springframework.core.LocalVariableTableParameterNameDiscoverer.LocalVariableTableVisitor.computeLvtSlotIndices(boolean, org.springframework.asm.Type[])
     */
    private static List<ArgumentMessage> calcArgumentMessages(boolean isStatic, Type[] paramTypes) {
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
        return Objects.equals(i, 0) ? index : calcPreIndex(index, paramTypes, i - 1);
    }

    private static int calcPreIndex(int index, Type[] paramTypes, int i) {
        int preIndex = calcIndex(index, paramTypes, i);
        return $.isWideType(paramTypes[i]) ? preIndex + 2 : preIndex + 1;
    }

    @Override
    public String toString() {
        return getName();
    }
}

