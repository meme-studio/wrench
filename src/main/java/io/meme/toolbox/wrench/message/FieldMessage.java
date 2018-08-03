package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.FieldResolver;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.NameUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author meme
 * @since 2018/7/27
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class FieldMessage extends FieldResolver implements Serializable {
    private static final long serialVersionUID = 2648987017868206269L;
    private String name;
    private String longTypeName;
    private int access;

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    private String getTypeName() {
        return NameUtils.calcSimpleClassName(longTypeName);
    }

}

