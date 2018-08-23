package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.classpath.ClassPathProvider;
import io.meme.toolbox.wrench.classpath.DefaultClassPathProvider;
import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.resolver.ClassResolver;
import io.meme.toolbox.wrench.resolver.ClassTypeResolver;
import io.meme.toolbox.wrench.resolver.JarResolver;
import io.meme.toolbox.wrench.utils.$;
import io.vavr.API;
import lombok.NoArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.Function;

/**
 * Wrench是一个基于ow2 asm驱动的类扫描与解析工具。
 * <p>{@link Wrench} 是整个工具的入口，提供了直接扫描 {@link Wrench#scanDirectly()}，区配/排除包扫描以及对扫面类/成员/方法的可见性做可控配置。
 *
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(staticName = "wrench")
public final class Wrench {

    private Configuration configuration = Configuration.preset();
    private List<ClassTypeResolver> resolvers = Arrays.asList(new ClassResolver(), new JarResolver());
    private List<ClassPathProvider> providers = Collections.singletonList(new DefaultClassPathProvider());

    /**
     * 只包含公开的类，成员变量与成员方法。
     */
    public static Result scanDirectly() {
        return wrench().scan();
    }

    public Wrench includeInvisibleMethod() {
        configuration.includeInvisibleMethod();
        return this;
    }

    public Wrench includeInvisibleClass() {
        configuration.includeInvisibleClass();
        return this;
    }

    public Wrench includeInvisibleField() {
        configuration.includeInvisibleField();
        return this;
    }

    public Wrench visible() {
        configuration.visible();
        return this;
    }

    public Wrench includePackages(String... packageNames) {
        configuration.setInclusionPackages(Arrays.asList(packageNames));
        return this;
    }


    public Wrench excludePackages(String... packageNames) {
        configuration.setExclusionPackages(Arrays.asList(packageNames));
        return this;
    }

    public Result scan() {
        return providers.stream()
                        .map(ClassPathProvider::listClassPaths)
                        .flatMap(Collection::stream)
                        .map(Paths::get)
                        .flatMap(API.<Path, Stream<Path>>unchecked(Files::walk))
                        .parallel()
                        .map(Path::toString)
                        .flatMap(this::calcClassMessage)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect($.toResult());
    }

    private Stream<ClassMessage> calcClassMessage(String path) {
        return resolvers.stream()
                        .filter(predicate(Function(ClassTypeResolver::isTypeMatched).reversed()
                                                                                    .apply(path)))
                        .flatMap(Function(ClassTypeResolver::resolve).reversed()
                                                                     .apply(configuration, path));
    }

}

