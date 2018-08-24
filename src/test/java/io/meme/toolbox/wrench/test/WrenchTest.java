package io.meme.toolbox.wrench.test;

import io.meme.toolbox.wrench.Result;
import io.meme.toolbox.wrench.Wrench;
import org.junit.jupiter.api.Test;

class WrenchTest {

    @Test
    void scanDirectly() {
    }

    @Test
    void includeInvisibleMethod() {
        Result result = Wrench.wrench()
                              .includeInvisibleMethod()
                              .includePackages("java.lang")
                              .scan();
        System.out.println();
    }

    @Test
    void includeInvisibleClass() {
    }

    @Test
    void includeInvisibleField() {
    }

    @Test
    void visible() {
        Result result = Wrench.wrench()
                              .visible()
                              .includePackages("java.lang")
                              .scan();
        System.out.println();
    }

    @Test
    void includePackages() {
    }

    @Test
    void excludePackages() {
    }

    @Test
    void scan() {
    }
}