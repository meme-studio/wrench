package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.MethodResolver;
import io.meme.toolbox.wrench.utils.AccessUtils;
import jdk.internal.org.objectweb.asm.Label;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author meme
 * @since 2018/7/23
 */
@AllArgsConstructor(staticName = "of")
public class MethodMessage extends MethodResolver implements Serializable {
    private static final long serialVersionUID = 1286151805906509943L;
    private final String name;
    private final String desc;
    private final int access;

    public boolean isAbstract() {
        return AccessUtils.isAbstract(access);
    }

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    //TODO
    public List<ArgumentMessage> getArgumentMessages() {
        return Collections.emptyList();
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
