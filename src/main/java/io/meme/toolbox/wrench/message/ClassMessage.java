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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        MethodMessage method = MethodMessage.of(name, desc, access);
        methodMessages.add(method);
        return method;
    }

}

