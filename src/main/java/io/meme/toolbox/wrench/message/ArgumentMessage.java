package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.utils.NameUtils;
import lombok.*;

import java.io.Serializable;

/**
 * @author meme
 * @since 2018/7/26
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class ArgumentMessage implements Serializable {
    private static final long serialVersionUID = -2807593115732931530L;
    private final String longTypeName;
    @Setter(AccessLevel.PACKAGE)
    private String argumentName;
    /**
     * Reference to Spring
     */
    @Getter(AccessLevel.PACKAGE)
    private final int lvtSlotIndex;
    public String getShortTypeName() {
        return NameUtils.calcSimpleClassName(longTypeName);
    }
}
