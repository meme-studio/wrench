package io.meme.toolbox.wrench.utils;

import io.meme.toolbox.wrench.Configuration;
import io.meme.toolbox.wrench.Result;
import io.meme.toolbox.wrench.message.ClassInfo;
import io.vavr.control.Option;
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

    public static ClassInfo getClassInfo(Configuration configuration, ClassReader reader) {
        return Option.of(configuration)
                     .map(ClassInfo::of)
                     .peek(classMessage -> calcMessage(reader, classMessage))
                     .getOrElseThrow(IllegalArgumentException::new);
    }

    private static void calcMessage(ClassReader reader, ClassInfo classMessage) {
        reader.accept(classMessage, ClassReader.SKIP_FRAMES);
    }

    public static Collector<ClassInfo, ?, Result> toResult() {
        return collectingAndThen(toList(), Result::of);
    }

    public static boolean isAnonymousClass(String className) {
        return className.matches("^.*[$]\\d+.*$");
    }

    public static boolean isTypeOf(String fileName, String fileType) {
        return fileName.regionMatches(true, fileName.length() - fileType.length(), fileType, 0, fileType.length());
    }

    public static boolean isClassFileType(String path) {
        return $.isTypeOf(path, ".class");
    }

}
