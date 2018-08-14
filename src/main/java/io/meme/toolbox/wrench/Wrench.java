package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.utils.$;
import io.vavr.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.negate;
import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.Function;
import static io.vavr.API.unchecked;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * Wrench是一个基于ow2 asm驱动的类扫描与解析工具。
 * <p>{@link Wrench} 是整个工具的入口，提供了直接扫描 {@link Wrench#scanDirectly()}，区配/排除包扫描以及对扫面类/成员/方法的可见性做可控配置。
 *
 * @author meme
 * @since 1.0
 */
@Accessors(fluent = true)
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "wrench")
public final class Wrench {

    private int visibility = $.INVISIBLE;
    private List<String> includePackages = singletonList("");
    private List<String> excludePackages = emptyList();

    /**
     *  对java.class.path下所有类进行扫描，只包含公开的类，成员变量与成员方法。
     */
    public static Result scanDirectly() {
        return wrench().scan();
    }

    public Wrench includeInvisibleMethod() {
        return visibility(visibility | $.INCLUDE_INVISIBLE_METHOD);
    }

    public Wrench includeInvisibleClass() {
        return visibility(visibility | $.INCLUDE_INVISIBLE_CLASS);
    }

    public Wrench includeInvisibleField() {
        return visibility(visibility | $.INCLUDE_INVISIBLE_FIELD);
    }

    public Wrench includeAllInvisible() {
        return visibility(visibility | $.INCLUDE_ALL_INVISIBLE);
    }

    @Tolerate
    public Wrench includePackages(String... packageNames) {
        return includePackages(Arrays.asList(packageNames));
    }

    @Tolerate
    public Wrench excludePackages(String... packageNames) {
        return excludePackages(Arrays.asList(packageNames));
    }

    public Result scan() {
        return Stream.of($.CLASSPATHS)
                     .flatMap(this::scan)
                     .filter(Objects::nonNull)
                     .distinct()
                     .collect($.toResult());
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
                    .filter(negate($::isAnonymousClass)
                            .and(predicate(Function($::matchPackages).apply(includePackages)))
                            .and(negate(Function($::matchPackages).apply(excludePackages))))
                    .map(API.<String, File>unchecked(File::new))
                    .map(unchecked(FileInputStream::new))
                    .map(Function($::determineClassMessage).apply(visibility));
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
                    .filter(predicate(Function($::isClassFileType).compose(JarEntry::getName))
                            .and(negate(Function($::isAnonymousClass).compose(JarEntry::getName)))
                            .and(predicate(Function($::matchPackages).apply(includePackages).compose(JarEntry::getName)))
                            .and(negate(Function($::matchPackages).apply(excludePackages).compose(JarEntry::getName))))
                    .map(Function($::getClassInputStream).apply(entry))
                    .map(Function($::determineClassMessage).apply(visibility));
    }

}

