package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.classpath.ClassPathProvider;
import io.meme.toolbox.wrench.classpath.DefaultClassPathProvider;
import io.meme.toolbox.wrench.message.ClassMessage;
import io.meme.toolbox.wrench.message.visitor.SimpleClassMessageVisitor;
import io.meme.toolbox.wrench.resolver.ClassFileResolver;
import io.meme.toolbox.wrench.resolver.ClassResolver;
import io.meme.toolbox.wrench.resolver.JarResolver;
import io.meme.toolbox.wrench.utils.$;
import io.meme.toolbox.wrench.utils.AccessUtils;
import io.vavr.API;
import jdk.internal.org.objectweb.asm.ClassReader;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static io.meme.toolbox.wrench.utils.Functions.predicate;
import static io.vavr.API.Function;
import static io.vavr.API.Try;

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
    private List<ClassFileResolver> resolvers = Arrays.asList(new ClassResolver(), new JarResolver());
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
                        .flatMap(this::calcClassMessage)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect($.toResult());
    }

    private Stream<ClassMessage> calcClassMessage(Path path) {
        return resolvers.stream()
                        .filter(predicate(Function(ClassFileResolver::isTypeMatched).reversed()
                                                                                    .apply(path)))
                        .flatMap(Function(ClassFileResolver::resolve).reversed().apply(path))
                        .map(Function(this::determineClassMessage));
    }


    private ClassMessage determineClassMessage(InputStream is) {
        return Try(() -> new ClassReader(is)).toOption()
                                             .filter(predicate(this::filter))
                                             .map(Function($::getClassMessage).apply(configuration))
                                             .getOrNull();
    }

    private boolean filter(ClassReader reader) {
        SimpleClassMessageVisitor visitor = getSimpleClassMessageVisitor(reader);
        return !$.isAnonymousClass(visitor.getName())
                && (!configuration.isPackageExcluded(visitor.getName()) && configuration.isPackageIncluded(visitor.getName()))
                && (configuration.isEnableVisibleClass() || AccessUtils.isPublic(reader.getAccess()));
    }

    private static SimpleClassMessageVisitor getSimpleClassMessageVisitor(ClassReader reader) {
        SimpleClassMessageVisitor visitor = new SimpleClassMessageVisitor();
        reader.accept(visitor, ClassReader.SKIP_FRAMES);
        return visitor;
    }

}

