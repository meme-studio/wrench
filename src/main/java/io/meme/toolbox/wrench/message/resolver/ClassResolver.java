package io.meme.toolbox.wrench.message.resolver;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 2018/7/30
 */
public abstract class ClassResolver extends ClassVisitor {
    protected ClassResolver() {
        super(Opcodes.ASM5);
    }
}
