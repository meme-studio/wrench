package io.meme.toolbox.wrench.utils;

import io.meme.toolbox.wrench.message.ClassMessage;
import jdk.internal.org.objectweb.asm.ClassReader;
import lombok.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.$.ClassFileType.*;
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
        JAR(".jar"), WAR(".war"), CLASS(".class");
        private String suffixName;
    }

    public static final String CLASSPATH = Objects.requireNonNull(System.getProperty("java.class.path"));

    public static final int INVISIBLE = 0;

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
                Case($(isIn(JAR.getSuffixName(), WAR.getSuffixName())), JAR),
                Case($(is(CLASS.getSuffixName())), CLASS),
                Case($(), () -> Asserts.fail("class file type")));
    }

    @SneakyThrows
    public static InputStream getClassInputStream(Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        return entry.getKey().getInputStream(jarEntry);
    }

    @SneakyThrows
    public static ClassMessage determineClassMessage(int ignoreVisibilities, InputStream is) {
        return Try(() -> new ClassReader(is)).toOption()
                                             .filter(Predicates.of(Function($::matchLimited).apply(ignoreVisibilities)))
                                             .map(Function($::getClassMessage).apply(ignoreVisibilities))
                                             .getOrNull();
    }

    private static boolean matchLimited(int ignoreVisibilities, ClassReader reader) {
        return isIgnoreClassVisibility(ignoreVisibilities) || AccessUtils.isPublic(reader.getAccess());
    }

    private static ClassMessage getClassMessage(int ignoreVisibilities, ClassReader reader) {
        ClassMessage classMessage = ClassMessage.of(ignoreVisibilities);
        reader.accept(classMessage, 0);
        return classMessage;
    }

    //TODO
    private static boolean isExtend(String superName, List<String> interfaces, ClassReader reader) {
        return Stream.concat(Stream.of(superName), interfaces.stream())
                     .allMatch(isIn(NameUtils.calcInternalNames(interfaces)).or(is(NameUtils.calcInternalName(getSuperName(reader)))));
    }

    private static String getSuperName(ClassReader reader) {
        return Objects.isNull(reader.getSuperName()) ? "java/lang/Object" : reader.getSuperName();
    }

    public static boolean matchPackages(List<String> packages, String path) {
        return packages.stream().anyMatch(NameUtils.calcInternalName(path)::contains);
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
