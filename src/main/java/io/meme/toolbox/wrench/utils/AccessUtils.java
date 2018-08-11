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
public final class AccessUtils {
    public static boolean isAbstract(int access) {
        return (Opcodes.ACC_ABSTRACT & access) > 0;
    }

    public static boolean isInterface(int access) {
        return (Opcodes.ACC_INTERFACE & access) > 0;
    }

    public static boolean isEnum(int access) {
        return (Opcodes.ACC_ENUM & access) > 0;
    }

    public static boolean isStatic(int access) {
        return ((Opcodes.ACC_STATIC) & access) > 0;
    }

    public static boolean isPublic(int access) {
        return ((Opcodes.ACC_PUBLIC) & access) > 0;
    }

    public static boolean isPrivate(int access) {
        return ((Opcodes.ACC_PRIVATE) & access) > 0;
    }

    public static boolean isProtected(int access) {
        return ((Opcodes.ACC_PROTECTED) & access) > 0;
    }

    public static boolean isSynchronized(int access) {
        return ((Opcodes.ACC_SYNCHRONIZED) & access) > 0;
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
