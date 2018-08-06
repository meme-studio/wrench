package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.Predicates;
import io.vavr.API;
import lombok.Builder;
import lombok.SneakyThrows;

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
@Builder
public final class Wrench {

    @Builder.Default
    private int ignoreVisibilities = $.INVISIBLE;

    @Builder.Default
    private List<String> includePackages = emptyList();

    @Builder.Default
    private List<String> excludePackages = emptyList();

    public static Wrench wrench() {
        return Wrench.builder().build();
    }

    public static Result scanDirectly() {
        return wrench().scan();
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
        return Stream.of($.CLASSPATH.split(File.pathSeparator))
                     .flatMap(this::scan)
                     .filter(Objects::nonNull)
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
                    .filter(Predicates.negate($::isAnonymousClass))
                    .filter(Predicates.of(Function($::matchPackages).apply(includePackages)))
                    .filter(Predicates.negate(Function($::matchPackages).apply(excludePackages)))
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
                    .filter(Predicates.of(Function($::isClassFileType).compose(JarEntry::getName)))
                    .filter(Predicates.negate(Function($::isAnonymousClass).compose(JarEntry::getName)))
                    .filter(Predicates.of(Function($::matchPackages).apply(includePackages).compose(JarEntry::getName)))
                    .filter(Predicates.negate(Function($::matchPackages).apply(excludePackages).compose(JarEntry::getName)))
                    .map(Function($::getClassInputStream).apply(entry))
                    .map(Function($::determineClassMessage).apply(ignoreVisibilities));
    }

}

