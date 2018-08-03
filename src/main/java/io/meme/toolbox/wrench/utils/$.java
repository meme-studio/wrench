package io.meme.toolbox.wrench.utils;

import io.meme.toolbox.wrench.message.ClassMessage;
import io.vavr.API;
import jdk.internal.org.objectweb.asm.ClassReader;
import lombok.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.$.ClassFileType.CLASS;
import static io.meme.toolbox.wrench.utils.$.ClassFileType.JAR;
import static io.vavr.API.$;
import static io.vavr.API.*;
import static io.vavr.Predicates.is;
import static io.vavr.Predicates.isIn;

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

    public static final int INVISIBLE = 0;

    public static final String CLASSPATH = System.getProperty("java.class.path");

    public static final int IGNORE_CLASS_VISIBILITY = 1;

    public static final int IGNORE_FIELD_VISIBILITY = 2;

    public static final int IGNORE_METHOD_VISIBILITY = 4;

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

    public static boolean isAnonymousClass(String path) {
        return path.matches("^.*[$]\\d+\\.class$");
    }

    public static ClassFileType determineClassFileType(String path) {
        return Match(getSuffixName(path)).of(
                API.Case(API.$(is(JAR.getSuffixName())), JAR),
                API.Case(API.$(is(CLASS.getSuffixName())), CLASS),
                Case($(), () -> {
                    throw new IllegalArgumentException("Illegal class file type!");
                })
        );
    }

    @SneakyThrows
    public static InputStream getClassInputStream(Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        return entry.getKey()
                    .getInputStream(jarEntry);
    }

    @SneakyThrows
    public static ClassMessage determineClassMessage(int ignoreVisibilities, String superName, List<String> interfaces, InputStream is) {
        return Try(() -> new ClassReader(is)).toOption()
                                             .toJavaOptional()
                                             .filter(reader -> (isIgnoreClassVisibility(ignoreVisibilities)
                                                             || AccessUtils.isPublic(reader.getAccess()))
                                                             && isExtend(superName, interfaces, reader))
                                             .map(reader -> getClassMessage(reader, ignoreVisibilities))
                                             .orElse(null);
    }

    public static boolean isExtend(String superName, List<String> interfaces, ClassReader reader) {
        return Stream.concat(Stream.of(superName), interfaces.stream())
                     .allMatch(isIn(Arrays.stream(reader.getInterfaces())
                                          .map(NameUtils::calcInternalName)
                                          .toArray())
                             .or(is(NameUtils.calcInternalName(getSuperName(reader)))));
    }

    private static String getSuperName(ClassReader reader) {
        return Objects.isNull(reader.getSuperName()) ? "java/lang/Object" : reader.getSuperName();
    }

    private static ClassMessage getClassMessage(ClassReader reader, int ignoreVisibilities) {
        ClassMessage classMessage = ClassMessage.of(ignoreVisibilities);
        reader.accept(classMessage, 0);
        return classMessage;
    }

    public static boolean isIgnoreClassVisibility(int ignoreVisibilities) {
        return (IGNORE_CLASS_VISIBILITY & ignoreVisibilities) > 0;
    }

    public static boolean isIgnoreFieldVisibility(int ignoreVisibilities) {
        return (IGNORE_FIELD_VISIBILITY & ignoreVisibilities) > 0;
    }


    public static boolean isIgnoreMethodVisibility(int ignoreVisibilities) {
        return (IGNORE_METHOD_VISIBILITY & ignoreVisibilities) > 0;
    }


}
