package io.meme.joke.classscanner.message;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ClassMessageTest {

    @Test
    void testCreateInstance() throws IOException {
        String internalName = Type.getObjectType("java/util/ArrayList").getClassName();
        ClassReader classReader = new ClassReader("java.util.ArrayList");
        ClassMessage classMessage = new ClassMessage();
        classReader.accept(classMessage, ClassReader.EXPAND_FRAMES);

        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        System.in.read();

    }

}