package io.meme.toolbox.wrench.message;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 2018/7/30
 */
abstract class ClassResolver extends ClassVisitor {
    ClassResolver() {
        super(Opcodes.ASM5);
    }
}
