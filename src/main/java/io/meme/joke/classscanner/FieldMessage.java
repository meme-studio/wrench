package io.meme.joke.classscanner;

import io.meme.joke.classscanner.utils.NameUtils;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author meme
 * @since 2018/7/27
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class FieldMessage extends FieldResolver {

    private static final long serialVersionUID = 2648987017868206269L;
    private String name;
    private String longTypeName;
    private boolean staticField;

    private String getTypeName() {
        return NameUtils.calcSimpleClassName(longTypeName);
    }

}

abstract class FieldResolver extends FieldVisitor {

    FieldResolver() {
        super(Opcodes.ASM5);
    }
}
