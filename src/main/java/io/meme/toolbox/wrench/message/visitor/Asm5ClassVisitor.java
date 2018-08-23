package io.meme.toolbox.wrench.message.visitor;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 1.0
 */
public abstract class Asm5ClassVisitor extends ClassVisitor {
    protected Asm5ClassVisitor() {
        super(Opcodes.ASM5);
    }
}
