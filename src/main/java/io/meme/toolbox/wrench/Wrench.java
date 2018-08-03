package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.utils.$;
import io.vavr.API;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Builder.Default
    private int ignoreVisibilities;

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

    public static Wrench includePackages(String... packageNames) {
        return Wrench.builder()
                     .includePackages(Arrays.asList(packageNames))
                     .build();
    }

    public static Wrench excludePackages(String... packageNames) {
        return Wrench.builder()
                     .excludePackages(Arrays.asList(packageNames))
                     .build();
    }

    public static Wrench disableJavaHomeScanning() {
        return Wrench.builder()
                     .javaHome(null)
                     .build();
    }

    public static Wrench disableClassPathScanning() {
        return Wrench.builder()
                     .classpath(null)
                     .build();
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
                    .map(Function($::determineClassMessage).apply(ignoreVisibilities));
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
                    .map(Function($::determineClassMessage).apply(ignoreVisibilities))
                    .filter(Objects::nonNull);
    }

}

