package io.meme.toolbox.wrench.message;

import io.meme.toolbox.wrench.utils.NameUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author meme
 * @since 2018/7/26
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class ArgumentMessage {
    private String longTypeName;
    private String argumentName;
    public String getShortTypeName() {
        return NameUtils.calcSimpleClassName(longTypeName);
    }
}
