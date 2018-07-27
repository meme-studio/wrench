package io.meme.joke.classscanner;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import lombok.extern.java.Log;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.function.Functions;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author meme
 * @since 2018/7/23
 */
@Log
public class ClassScanner {


    static {
        index();
    }

    private static void index() {

    }


    public static List<ClassMessage> complete(String keyword) throws IOException {
        return complete(keyword, ScanMode.SMART);
    }

    private static List<ClassMessage> complete(String keyword, ScanMode mode) {

        return null;
    }

    public static void main(String[] args) throws RunnerException, IOException {
//        Options opt = new OptionsBuilder().include(CodeCompleter.class.getSimpleName())
//                                          .forks(1)
//                                          .build();
//
//        new Runner(opt).run();
        testScanJavaHome();

    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public static void testScanJavaHome() throws IOException {
        String javaHome = System.getProperty("java.home");
        Map<String, ClassNode> collect = Files.walk(Paths.get(javaHome))
                                              .parallel()
                                              .filter(path -> path.toString().endsWith(".jar"))
                                              .map(Path::toString)
                                              .map(Unchecked.function(JarFile::new))
                                              .collect(Collectors.collectingAndThen(Collectors.toMap(Function.identity(), JarFile::stream),
                                                      result -> result.entrySet()
                                                                      .parallelStream()
                                                                      .flatMap(entry -> entry.getValue()
                                                                                             .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                                                                                             .map(Unchecked.function(jarEntry -> entry.getKey().getInputStream(jarEntry)))
                                                                                             .map(Unchecked.function(ClassScanner::determineClassNode))
                                                                                             .filter(Functions.<ClassNode>not(node -> node.name.contains("$"))
                                                                                                              .or(node -> node.name.startsWith("$"))))
                                                                      .collect(Collectors.toMap(node -> node.name, Function.identity(), (o1, o2) -> o2))
                                              ));
        System.out.println(collect);
    }

    private static ClassNode determineClassNode(InputStream is) throws IOException {
        ClassReader reader = new ClassReader(is);
        ClassNode node = new ClassNode();
        reader.accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        return node;
    }
}

