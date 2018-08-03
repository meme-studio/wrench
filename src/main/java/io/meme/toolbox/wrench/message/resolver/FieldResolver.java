package io.meme.toolbox.wrench.message.resolver;

import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 2018/7/30
 */
public abstract class FieldResolver extends FieldVisitor {
    protected FieldResolver() {
        super(Opcodes.ASM5);
    }
}
