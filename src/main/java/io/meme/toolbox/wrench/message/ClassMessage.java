package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.ClassResolver;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.NameUtils;
import jdk.internal.org.objectweb.asm.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author meme
 * @since 2018/7/23
 */
@Getter
public class ClassMessage extends ClassResolver implements Serializable {
    private static final long serialVersionUID = -5621028783726663753L;
    private String name;
    private int access;
    private String signature;
    private List<MethodMessage> methodMessages = new ArrayList<>();
    private List<FieldMessage> fieldMessages = new ArrayList<>();
    private int ignoreVisibilities;

    public List<String> getInterfaceNames() {
        return NameUtils.calcInterfaceNames(signature);
    }

    public String getSuperClassName() {
        return NameUtils.calcSuperClassName(signature);
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
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldMessage field = FieldMessage.of(name, desc, access);
        fieldMessages.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodMessage method = MethodMessage.of(name, desc, access);
        methodMessages.add(method);
        return method;
    }

}

