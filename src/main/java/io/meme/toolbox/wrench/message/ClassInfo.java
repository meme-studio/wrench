package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.message.visitor.SimpleClassInfo;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author meme
 * @since 1.0
 */
@RequiredArgsConstructor(staticName = "of")
public class ClassInfo extends SimpleClassInfo implements Serializable {
    private static final long serialVersionUID = -5621028783726663753L;

    @Getter
    private List<MethodInfo> methodInfos = new ArrayList<>();
    @Getter
    private List<FieldInfo> fieldInfos = new ArrayList<>();

    private final Configuration configuration;

    //FIXME for array type
    public static ClassInfo of(Class<?> clazz) {
        return of(clazz.getTypeName());
    }

    @SneakyThrows
    public static ClassInfo of(String className) {
        return $.getClassInfo(Configuration.preset(), new ClassReader(className));
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
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return visitAndReturn(access, name, desc, this::calcFieldMessage, ignore -> configuration.isEnableVisibleField());
    }

    private FieldInfo calcFieldMessage(String name, String desc, int access) {
        FieldInfo field = FieldInfo.of(name, NameUtils.calcInternalName(Type.getType(desc).getClassName()), access);
        fieldInfos.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return visitAndReturn(access, name, desc, this::calcMethodMessage, ignore -> configuration.isEnableVisibleMethod());
    }

    private <T> T visitAndReturn(int access, String name, String desc, Function3<String, String, Integer, T> messageGenerator, Predicate<Integer> isAccessIgnore) {
        return Option.of(access)
                     .filter(Predicates.anyOf(isAccessIgnore, AccessUtils::isPublic)
                                       .and(Predicates.noneOf(AccessUtils::isSynthetic)))
                     .map(messageGenerator.apply(name, desc))
                     .getOrNull();
    }

    private MethodInfo calcMethodMessage(String name, String desc, int access) {
        List<ArgumentInfo> argumentMessages = calcArgumentMessages(AccessUtils.isStatic(access), Type.getArgumentTypes(desc));
        MethodInfo method = MethodInfo.of(getName(), name, NameUtils.calcInternalName(Type.getReturnType(desc).getClassName()), access, argumentMessages);
        methodInfos.add(method);
        return method;
    }

    /**
     * Reference to Spring org.springframework.core.LocalVariableTableParameterNameDiscoverer.LocalVariableTableVisitor.computeLvtSlotIndices(boolean, org.springframework.asm.Type[])
     */
    private static List<ArgumentInfo> calcArgumentMessages(boolean isStatic, Type[] paramTypes) {
        return IntStream.range(0, paramTypes.length)
                        .boxed()
                        .map(Function3.of(ClassInfo::calcArgumentMessage)
                                      .apply(isStatic ? 0 : 1, paramTypes))
                        .collect(Collectors.toList());
    }

    private static ArgumentInfo calcArgumentMessage(int index, Type[] paramTypes, int i) {
        return ArgumentInfo.of(paramTypes[i].getClassName(), String.format("arg%d", i), calcIndex(index, paramTypes, i));
    }

    private static int calcIndex(int index, Type[] paramTypes, int i) {
        return Objects.equals(i, 0) ? index : calcPreIndex(index, paramTypes, i - 1);
    }

    private static int calcPreIndex(int index, Type[] paramTypes, int i) {
        int preIndex = calcIndex(index, paramTypes, i);
        return isWideType(paramTypes[i]) ? preIndex + 2 : preIndex + 1;
    }


    private static boolean isWideType(Type type) {
        return Objects.equals(type, Type.LONG_TYPE) || Objects.equals(type, Type.DOUBLE_TYPE);
    }

}

