package io.meme.toolbox.wrench.message.resolver;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 2018/7/30
 */
public abstract class MethodResolver extends MethodVisitor {
    protected MethodResolver() {
        super(Opcodes.ASM5);
    }
}
