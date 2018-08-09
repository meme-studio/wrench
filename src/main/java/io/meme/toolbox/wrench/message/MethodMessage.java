package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.message.resolver.MethodResolver;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.meme.toolbox.wrench.utils.Predicates;
import io.vavr.Function2;
import jdk.internal.org.objectweb.asm.Label;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    @Getter
    private final List<ArgumentMessage> argumentMessages;

    public boolean isAbstract() {
        return AccessUtils.isAbstract(access);
    }

    public boolean isStatic() {
        return AccessUtils.isStatic(access);
    }

    //TODO
    public String getDisplayDesc() {
        return null;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        argumentMessages.stream()
                        .filter(Predicates.of(Function2.of(Objects::equals)
                                                       .apply(index)
                                                       .compose(ArgumentMessage::getLvtSlotIndex)))
                        .findAny()
                        .ifPresent(argumentMessage -> argumentMessage.setArgumentName(name));
    }

}
