package io.meme.toolbox.wrench.message.visitor;

import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 1.0
 */
public abstract class Asm5FieldVisitor extends FieldVisitor {
    protected Asm5FieldVisitor() {
        super(Opcodes.ASM5);
    }
}
