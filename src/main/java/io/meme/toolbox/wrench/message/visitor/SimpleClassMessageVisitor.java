package io.meme.toolbox.wrench.message.visitor;

import io.meme.toolbox.wrench.utils.NameUtils;
import io.vavr.control.Option;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author meme
 * @since 1.0
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SimpleClassMessageVisitor extends Asm5ClassVisitor {
    @EqualsAndHashCode.Include
    @Getter
    protected String name;
    protected int access;
    @Getter
    private String superClassName;
    @Getter
    private List<String> interfaceNames;

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.name = NameUtils.calcInternalName(name);
        this.access = access;
        this.superClassName = Option.of(superName)
                                    .filter(Objects::nonNull)
                                    .map(NameUtils::calcInternalName)
                                    .getOrNull();
        this.interfaceNames = Arrays.asList(NameUtils.calcInternalNames(interfaces));
    }

    @Override
    public String toString() {
        return getName();
    }
}
