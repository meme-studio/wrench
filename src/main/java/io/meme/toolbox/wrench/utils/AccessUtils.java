package io.meme.toolbox.wrench.utils;

import jdk.internal.org.objectweb.asm.Opcodes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static io.vavr.API.$;
import static io.vavr.API.*;

/**
 * @author meme
 * @since 2018/7/30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessUtils {
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

    private static boolean is(Integer access, int accessType) {
        return (accessType & access) > 0;
    }

    public static String getAccessType(int access) {
        return Match(access).of(
                Case($(AccessUtils::isPublic), "public"),
                Case($(AccessUtils::isPrivate), "private"),
                Case($(AccessUtils::isProtected), "protected"),
                Case($(), "")
        );
    }

    public static String getStaticOrAbstract(int access) {
        return Match(access).of(
                Case($(AccessUtils::isStatic), "static"),
                Case($(AccessUtils::isAbstract), "abstract"),
                Case($(), "")
        );
    }

    public static String getSynchronized(int access) {
        return Match(access).of(
                Case($(AccessUtils::isSynchronized), "synchronized"),
                Case($(), "")
        );
    }
}
