package io.meme.joke.classscanner.message;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 2018/7/30
 */
abstract class MethodResolver extends MethodVisitor {
    MethodResolver() {
        super(Opcodes.ASM5);
    }
}
