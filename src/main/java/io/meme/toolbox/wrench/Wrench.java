package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.Predicates;
import io.vavr.API;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.vavr.API.Function;
import static io.vavr.API.unchecked;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * @author meme
 * @since 2018/7/23
 */
@Log
@Builder
public final class Wrench {

    private int ignoreVisibilities;

    @Builder.Default
    private String superClass = "java.lang.Object";

    @Builder.Default
    private List<String> interfaces = emptyList();

    @Builder.Default
    private List<String> includePackages = emptyList();

    @Builder.Default
    private List<String> excludePackages = emptyList();

    @Builder.Default
    private String javaHome = System.getProperty("java.home");

    @Builder.Default
    private String classpath = Objects.requireNonNull(Thread.currentThread()
                                                            .getContextClassLoader()
                                                            .getResource("/"))
                                      .getPath();


    public static Wrench allPackages() {
        return Wrench.builder().build();
    }

    public static Wrench disableJavaHome() {
        return Wrench.builder().javaHome(null).build();
    }

    public static Wrench disableClassPath() {
        return Wrench.builder().classpath(null).build();
    }

    public Wrench superClass(Class<?> superClass) {
        Optional.of(Objects.requireNonNull(superClass))
                .filter(Class::isInterface)
                .ifPresent(clazz -> {
                    throw new IllegalArgumentException("Class " + clazz.getName() + " is an interface!");
                });
        this.superClass = superClass.getName();
        return this;
    }

    public Wrench interfaces(Class<?>... interfaces) {
        Arrays.stream(interfaces)
              .filter((Predicates.negate(Class::isInterface)))
              .findAny()
              .ifPresent(clazz -> {
                  throw new IllegalArgumentException("Class " + clazz.getName() + " is not an interface!");
              });
        this.interfaces = Arrays.stream(interfaces).map(Class::getName).collect(toList());
        return this;
    }

    public Wrench includePackages(String... packageNames) {
        includePackages = Arrays.asList(packageNames);
        return this;
    }

    public Wrench excludePackages(String... packageNames) {
        excludePackages = Arrays.asList(packageNames);
        return this;
    }

    public Wrench ignoreMethodVisibility() {
        ignoreVisibilities |= $.IGNORE_METHOD_VISIBILITY;
        return this;
    }

    public Wrench ignoreClassVisibility() {
        ignoreVisibilities |= $.IGNORE_CLASS_VISIBILITY;
        return this;
    }

    public Wrench ignoreFieldVisibility() {
        ignoreVisibilities |= $.IGNORE_FIELD_VISIBILITY;
        return this;
    }

    public Wrench ignoreVisibility() {
        return ignoreFieldVisibility().ignoreClassVisibility().ignoreMethodVisibility();
    }

    public Result scan() {
        return Stream.of(classpath, javaHome)
                     .filter(Objects::nonNull)
                     .flatMap(this::scan)
                     .distinct()
                     .collect(collectingAndThen(toMap(ClassMessage::getName, identity()), Result::of));
    }

    @SneakyThrows
    private Stream<ClassMessage> scan(String path) {
        return Files.walk(Paths.get(path))
                    .parallel()
                    .map(Path::toString)
                    .filter($::isClassFileType)
                    .collect(collectingAndThen(groupingBy($::determineClassFileType), this::scan));
    }

    private Stream<ClassMessage> scan(Map<$.ClassFileType, List<String>> pathGroup) {
        return Stream.concat(
                scanJarType(pathGroup.getOrDefault($.ClassFileType.JAR, emptyList())),
                scanClassType(pathGroup.getOrDefault($.ClassFileType.CLASS, emptyList()))
        );
    }

    private Stream<ClassMessage> scanClassType(List<String> paths) {
        return paths.stream()
                    .map(API.<String, File>unchecked(File::new))
                    .map(unchecked(FileInputStream::new))
                    .map(Function($::determineClassMessage).apply(ignoreVisibilities, superClass, interfaces));
    }

    private Stream<ClassMessage> scanJarType(List<String> paths) {
        return paths.stream()
                    .map(API.<String, JarFile>unchecked(JarFile::new))
                    .collect(collectingAndThen(toMap(identity(), JarFile::stream), this::determineClassMessages));
    }

    private Stream<ClassMessage> determineClassMessages(Map<JarFile, Stream<JarEntry>> jars) {
        return jars.entrySet()
                   .parallelStream()
                   .flatMap(this::forEachEntry);
    }

    private Stream<ClassMessage> forEachEntry(Map.Entry<JarFile, Stream<JarEntry>> entry) {
        return entry.getValue()
                    .filter(jarEntry -> $.isClassFileType(jarEntry.getName()))
                    .map(Function($::getClassInputStream).apply(entry))
                    .map(Function($::determineClassMessage).apply(ignoreVisibilities, superClass, interfaces))
                    .filter(Objects::nonNull);
    }

}

