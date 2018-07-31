package io.meme.joke.classscanner.utils;

import io.meme.joke.classscanner.message.ClassMessage;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import lombok.*;

import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.joke.classscanner.utils.$.ClassFileType.CLASS;
import static io.meme.joke.classscanner.utils.$.ClassFileType.JAR;
import static io.vavr.API.$;
import static io.vavr.API.*;
import static io.vavr.Predicates.is;

/**
 * @author meme
 * @since 2018/7/31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class $ {

    @AllArgsConstructor
    @Getter
    public enum ClassFileType {
        JAR(".jar"), CLASS(".class");
        private String suffixName;
    }

    public static boolean isClassFileType(String path) {
        return isJarType(path) || isClassType(path);
    }

    public static boolean isClassType(String path) {
        return path.endsWith(CLASS.getSuffixName());
    }

    public static boolean isJarType(String path) {
        return path.endsWith(JAR.getSuffixName());
    }

    public static String getSuffixName(String path) {
        return path.substring(path.lastIndexOf("."));
    }

    public static ClassFileType getClassFileType(String path) {
        return Match(getSuffixName(path)).of(
                Case($(is(JAR.getSuffixName())), JAR),
                Case($(is(CLASS.getSuffixName())), CLASS),
                Case($(), () -> {throw new IllegalArgumentException("Illegal class file type!");})
        );
    }

    @SneakyThrows
    public static InputStream getClassInputStream(Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        return entry.getKey()
                    .getInputStream(jarEntry);
    }

    //TODO
    @SneakyThrows
    public static ClassMessage determineClassMessage(InputStream is) {
        ClassReader reader = new ClassReader(is);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        return null;
    }


}
