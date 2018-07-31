package io.meme.joke.classscanner.message;

import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author meme
 * @since 2018/7/30
 */
abstract class FieldResolver extends FieldVisitor {

    FieldResolver() {
        super(Opcodes.ASM5);
    }
}
