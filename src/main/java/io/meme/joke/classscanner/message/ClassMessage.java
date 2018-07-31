package io.meme.joke.classscanner.message;

import io.meme.joke.classscanner.utils.AccessUtils;
import io.meme.joke.classscanner.utils.NameUtils;
import jdk.internal.org.objectweb.asm.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meme
 * @since 2018/7/23
 */
@Getter
public class ClassMessage extends ClassResolver {
    private static final long serialVersionUID = -5621028783726663753L;
    private String name;
    private int access;
    private List<MethodMessage> methodMessages = new ArrayList<>();
    private List<FieldMessage> fieldMessages = new ArrayList<>();

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
        this.access = access;
    }

    //TODO
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldMessage field = FieldMessage.of(name, desc, access);
        fieldMessages.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodMessage method = MethodMessage.of(name, desc, new ArrayList<>(), access);
        methodMessages.add(method);
        return method;
    }

}

