package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.utils.NameUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author meme
 * @since 2018/7/26
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class ArgumentMessage implements Serializable {
    private static final long serialVersionUID = -2807593115732931530L;
    private String longTypeName;
    private String argumentName;
    public String getShortTypeName() {
        return NameUtils.calcSimpleClassName(longTypeName);
    }
}
