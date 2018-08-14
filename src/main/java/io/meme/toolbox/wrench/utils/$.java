package io.meme.toolbox.wrench.utils;

import io.meme.toolbox.wrench.Result;
import io.meme.toolbox.wrench.message.ClassMessage;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.Type;
import lombok.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.$.ClassFileType.*;
import static io.meme.toolbox.wrench.utils.Functions.*;
import static io.vavr.API.*;
import static io.vavr.Predicates.*;
import static java.util.stream.Collectors.*;

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

    public static final String[] CLASSPATHS = System.getProperty("java.class.path").split(File.pathSeparator);

    public static final int INVISIBLE = 0;

    public static final int INVISIBLE_CLASS = 1;

    public static final int INVISIBLE_FIELD = 2;

    public static final int INVISIBLE_METHOD = 4;

    public static final int VISIBLE = INVISIBLE_CLASS | INVISIBLE_FIELD | INVISIBLE_METHOD;

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
        return path.matches("^.*[$]\\d+.*$");
    }

    public static ClassFileType determineClassFileType(String path) {
        return Match(getSuffixName(path)).of(
                Case($(isIn(JAR.getSuffixName(), WAR.getSuffixName())), JAR),
                Case($(is(CLASS.getSuffixName())), CLASS),
                Case($(), () -> Asserts.fail("class file type"))
        );
    }

    @SneakyThrows
    public static InputStream getClassInputStream(Map.Entry<JarFile, Stream<JarEntry>> entry, JarEntry jarEntry) {
        return entry.getKey().getInputStream(jarEntry);
    }


    public static ClassMessage determineClassMessage(int ignoreVisibilities, InputStream is) {
        return Try(() -> new ClassReader(is)).toOption()
                                             .filter(predicate(Function($::matchLimited).apply(ignoreVisibilities)))
                                             .map(Function($::getClassMessage).apply(ignoreVisibilities))
                                             .getOrNull();
    }

    private static boolean matchLimited(int ignoreVisibilities, ClassReader reader) {
        return isClassVisibilityIgnored(ignoreVisibilities) || AccessUtils.isPublic(reader.getAccess());
    }

    public static ClassMessage getClassMessage(int ignoreVisibilities, ClassReader reader) {
        ClassMessage classMessage = ClassMessage.of(ignoreVisibilities);
        reader.accept(classMessage, ClassReader.SKIP_FRAMES);
        return classMessage;
    }

    public static boolean isWideType(Type type) {
        return Objects.equals(type, Type.LONG_TYPE) || Objects.equals(type, Type.DOUBLE_TYPE);
    }

    public static boolean matchPackages(List<String> packages, String path) {
        return packages.stream().anyMatch(NameUtils.calcInternalName(path)::contains);
    }

    public static List<String> listClassNames(Class<?>... className) {
        return Stream.of(className)
                     .map(Class::getName)
                     .collect(toList());
    }

    public static Collector<ClassMessage, ?, Result> toResult() {
        return collectingAndThen(toList(), Result::of);
    }


    public static boolean isClassVisibilityIgnored(int visibility) {
        return (INVISIBLE_CLASS & visibility) > 0;
    }

    public static boolean isEnableVisibleField(int visibility) {
        return (INVISIBLE_FIELD & visibility) > 0;
    }


    public static boolean isEnableVisibleMethod(int visibility) {
        return (INVISIBLE_METHOD & visibility) > 0;
    }


}
