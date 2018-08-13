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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.*;
import static io.vavr.API.Function;
import static io.vavr.API.unchecked;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * @author meme
 * @since 2018/7/23
 */
@Accessors(fluent = true)
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "wrench")
public final class Wrench {

    private int ignoreVisibilities = $.INVISIBLE;
    private List<String> includePackages, excludePackages = emptyList();

    public static Result scanDirectly() {
        return wrench().scan();
    }

    public Wrench ignoreMethodVisibility() {
        return ignoreVisibilities(ignoreVisibilities | $.IGNORE_METHOD_VISIBILITY);
    }

    public Wrench ignoreClassVisibility() {
        return ignoreVisibilities(ignoreVisibilities | $.IGNORE_CLASS_VISIBILITY);
    }

    public Wrench ignoreFieldVisibility() {
        return ignoreVisibilities(ignoreVisibilities | $.IGNORE_FIELD_VISIBILITY);
    }

    public Wrench ignoreVisibilities() {
        return ignoreVisibilities(ignoreVisibilities | $.IGNORE_VISIBILITIES);
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
                    .filter(negate($::isAnonymousClass))
                    .filter(predicate(Function($::matchPackages).apply(includePackages)))
                    .filter(negate(Function($::matchPackages).apply(excludePackages)))
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
                    .filter(predicate(Function($::isClassFileType).compose(JarEntry::getName)))
                    .filter(negate(Function($::isAnonymousClass).compose(JarEntry::getName)))
                    .filter(predicate(Function($::matchPackages).apply(includePackages).compose(JarEntry::getName)))
                    .filter(negate(Function($::matchPackages).apply(excludePackages).compose(JarEntry::getName)))
                    .map(Function($::getClassInputStream).apply(entry))
                    .map(Function($::determineClassMessage).apply(ignoreVisibilities));
    }

}

