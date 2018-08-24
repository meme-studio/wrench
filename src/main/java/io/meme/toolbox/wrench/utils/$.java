package io.meme.toolbox.wrench.utils;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.Result;
import io.meme.toolbox.wrench.message.ClassMessage;
import jdk.internal.org.objectweb.asm.ClassReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class $ {

    public static ClassMessage getClassMessage(Configuration configuration, ClassReader reader) {
        ClassMessage classMessage = ClassMessage.of(configuration);
        reader.accept(classMessage, ClassReader.SKIP_FRAMES);
        return classMessage;
    }

    public static Collector<ClassMessage, ?, Result> toResult() {
        return collectingAndThen(toList(), Result::of);
    }

    public static boolean isAnonymousClass(String className) {
        return className.matches("^.*[$]\\d+.*$");
    }


}
