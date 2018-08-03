package io.meme.toolbox.wrench;

import io.meme.toolbox.wrench.utils.$;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class WrenchTest {

    @Test
    void scan() throws IOException {
        ClassReader classReader = new ClassReader("java.util.ArrayList");
        ClassNode classVisitor = new ClassNode();
        classReader.accept(classVisitor, 0);
        boolean extend = $.isExtend("java.lang.Object", Arrays.asList("java.util.List"), classReader);
        System.out.println(extend);
        Result scan = Wrench.wrench().interfaces(List.class).scan();
        System.in.read();
    }
}