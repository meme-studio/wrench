package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @author meme
 * @since 2018/7/30
 */
@Getter
@AllArgsConstructor(staticName = "of")
public class Result {
    private final Map<String, ClassMessage> classMessages;
}
