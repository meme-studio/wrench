package io.meme.joke.classscanner.message;

import io.meme.joke.classscanner.utils.AccessUtils;
import jdk.internal.org.objectweb.asm.Label;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * @author meme
 * @since 2018/7/23
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class MethodMessage extends MethodResolver implements Serializable {
    private static final long serialVersionUID = 1286151805906509943L;
    private String name;
    private String desc;
    private List<ArgumentMessage> argumentMessages;
    private int access;

    public boolean isAbstract() {
        return AccessUtils.isAbstract(access);
    }

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    //TODO
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    //TODO
    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
    }

    //TODO
    public String getDisplayDesc() {
        return null;
    }

}
