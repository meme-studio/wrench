package io.meme.joke.classscanner;

import io.meme.joke.classscanner.message.ClassMessage;
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
    private Map<String, ClassMessage> classMessages;
}
