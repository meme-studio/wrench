package io.meme.joke.classscanner.utils;

import jdk.internal.org.objectweb.asm.Opcodes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author meme
 * @since 2018/7/30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessUtils {
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
}
