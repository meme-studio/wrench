package io.meme.joke.classscanner;

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
    private boolean enumClass;
    private boolean interfaceClass;
    private List<MethodMessage> methodMessages = new ArrayList<>();
    private List<FieldMessage> fieldMessages = new ArrayList<>();

    public String getSimpleName() {
        return NameUtils.calcSimpleClassName(name);
    }

    public String getPackageName() {
        return NameUtils.calcPackageName(name);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = NameUtils.calcInternalName(name);
        this.enumClass = isEnumClass(access);
        this.interfaceClass = isInterfaceClass(access);
    }

    //TODO
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldMessage field = FieldMessage.of(name, desc, isStatic(access));
        fieldMessages.add(field);
        return field;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodMessage method = MethodMessage.of(name, desc, new ArrayList<>(), isStatic(access));
        methodMessages.add(method);
        return method;
    }

    private boolean isInterfaceClass(int access) {
        return (Opcodes.ACC_INTERFACE & access) > 0;
    }

    private boolean isEnumClass(int access) {
        return (Opcodes.ACC_ENUM & access) > 0;
    }

    private static boolean isStatic(int access) {
        return ((access & Opcodes.ACC_STATIC) > 0);
    }

}

abstract class ClassResolver extends ClassVisitor {

    ClassResolver() {
        super(Opcodes.ASM5);
    }
}
