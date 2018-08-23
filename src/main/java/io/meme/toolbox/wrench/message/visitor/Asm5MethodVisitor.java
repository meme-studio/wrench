package io.meme.toolbox.wrench.message.visitor;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 1.0
 */
public abstract class Asm5MethodVisitor extends MethodVisitor {
    protected Asm5MethodVisitor() {
        super(Opcodes.ASM5);
    }
}
