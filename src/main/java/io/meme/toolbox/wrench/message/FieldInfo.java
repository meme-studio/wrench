package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.visitor.Asm5FieldVisitor;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.NameUtils;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Modifier;

/**
 * @author meme
 * @since 1.0
 */
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class FieldInfo extends Asm5FieldVisitor implements Serializable {
    private static final long serialVersionUID = 2648987017868206269L;
    @Getter
    private final String name;
    @Getter
    private final String typeName;
    private final int access;

    public boolean isFinal() {
        return AccessUtils.isFinal(access);
    }

    public boolean isVolatile() {
        return AccessUtils.isVolatile(access);
    }

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    public boolean isTransient() {
        return AccessUtils.isTransient(access);
    }

    private String getSimpleTypeName() {
        return NameUtils.calcSimpleClassName(typeName);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", Modifier.toString(access), typeName, name);
    }
}

