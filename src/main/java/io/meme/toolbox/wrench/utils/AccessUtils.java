package io.meme.toolbox.wrench.utils;

import jdk.internal.org.objectweb.asm.Opcodes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccessUtils {
    public static boolean isAbstract(int access) {
        return is(Opcodes.ACC_ABSTRACT, access);
    }

    public static boolean isInterface(int access) {
        return is(Opcodes.ACC_INTERFACE, access);
    }

    public static boolean isEnum(int access) {
        return is(Opcodes.ACC_ENUM, access);
    }

    public static boolean isStatic(int access) {
        return is(Opcodes.ACC_STATIC, access);
    }

    public static boolean isPublic(int access) {
        return is(Opcodes.ACC_PUBLIC, access);
    }

    public static boolean isPrivate(int access) {
        return is(Opcodes.ACC_PRIVATE, access);
    }

    public static boolean isProtected(int access) {
        return is(Opcodes.ACC_PROTECTED, access);
    }

    public static boolean isPackage(int access) {
        return !(isPublic(access) && isPrivate(access) && isProtected(access));
    }

    public static boolean isSynchronized(int access) {
        return is(Opcodes.ACC_SYNCHRONIZED, access);
    }

    public static boolean isVarargs(int access) {
        return is(Opcodes.ACC_VARARGS, access);
    }

    public static boolean isVolatile(int access) {
        return is(Opcodes.ACC_VOLATILE, access);
    }

    public static boolean isSynthetic(int access) {
        return is(Opcodes.ACC_SYNTHETIC, access);
    }

    public static boolean isFinal(int access) {
        return is(Opcodes.ACC_FINAL, access);
    }

    public static boolean isTransient(int access) {
        return is(Opcodes.ACC_TRANSIENT, access);
    }

    private static boolean is(Integer access, int accessType) {
        return (accessType & access) > 0;
    }

}
