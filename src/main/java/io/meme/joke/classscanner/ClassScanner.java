package io.meme.joke.classscanner;

import io.meme.joke.classscanner.message.ClassMessage;
import io.meme.joke.classscanner.utils.$;
import io.vavr.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
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

import static io.meme.joke.classscanner.utils.$.ClassFileType.CLASS;
import static io.meme.joke.classscanner.utils.$.ClassFileType.JAR;
import static io.vavr.API.*;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * @author meme
 * @since 2018/7/23
 */
@Log
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassScanner {

    private boolean ignoreMethodVisibility;

    private boolean ignoreClassVisibility;

    private boolean ignoreFieldVisibility;

    private List<String> includePackages;

    private List<String> excludePackages;

    private String javaHome = System.getProperty("java.home");

    private String classpath = Objects.requireNonNull(Thread.currentThread()
                                                            .getContextClassLoader()
                                                            .getResource("/"))
                                      .getPath();

    public static ClassScanner allPackages() {
        return initialPackagesScanning().build();
    }

    public static ClassScanner includePackages(String... packageNames) {
        return initialPackagesScanning().includePackages(Arrays.asList(packageNames))
                                        .build();
    }

    public static ClassScanner excludePackages(String... packageNames) {
        return initialPackagesScanning().excludePackages(Arrays.asList(packageNames))
                                        .build();
    }

    public static ClassScanner disableJavaHomeScanning() {
        return initialPackagesScanning()
                .javaHome(null)
                .build();
    }

    public static ClassScanner disableClassPathScanning() {
        return initialPackagesScanning()
                .classpath(null)
                .build();
    }

    private static ClassScannerBuilder initialPackagesScanning() {
        return ClassScanner.builder()
                           .includePackages(emptyList())
                           .excludePackages(emptyList());
    }

    public ClassScanner ignoreMethodVisibility() {
        ignoreMethodVisibility = true;
        return this;
    }

    public ClassScanner ignoreClassVisibility() {
        ignoreClassVisibility = true;
        return this;
    }

    public ClassScanner ignoreFieldVisibility() {
        ignoreFieldVisibility = true;
        return this;
    }

    public ClassScanner ignoreVisibility() {
        return this.ignoreFieldVisibility()
                   .ignoreClassVisibility()
                   .ignoreMethodVisibility();
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
                    .collect(collectingAndThen(groupingBy($::getClassFileType), this::scan));
    }

    private Stream<ClassMessage> scan(Map<$.ClassFileType, List<String>> pathGroup) {
        return Stream.concat(
                scanJarType(pathGroup.getOrDefault(JAR, emptyList())),
                scanClassType(pathGroup.getOrDefault(CLASS, emptyList()))
        );
    }

    private Stream<ClassMessage> scanClassType(List<String> paths) {
        return paths.stream()
                    .map(API.<String, File>unchecked(File::new))
                    .map(unchecked(FileInputStream::new))
                    .map($::determineClassMessage);
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
                    .map($::determineClassMessage);
    }

}

