package io.meme.joke.classscanner.message;

import io.meme.joke.classscanner.utils.AccessUtils;
import io.meme.joke.classscanner.utils.NameUtils;
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
    private int access;

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    private String getTypeName() {
        return NameUtils.calcSimpleClassName(longTypeName);
    }

}

