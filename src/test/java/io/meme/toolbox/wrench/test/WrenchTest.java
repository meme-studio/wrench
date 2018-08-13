package io.meme.toolbox.wrench.test;

import io.meme.toolbox.wrench.Result;
import io.meme.toolbox.wrench.Wrench;
import org.junit.jupiter.api.Test;

class WrenchTest {

    @Test
    void scanDirectly() {
        Result result = Wrench.scanDirectly();
        System.out.println();
    }

    @Test
    void ignoreMethodVisibility() {
        Result result = Wrench.wrench()
                              .ignoreMethodVisibility()
                              .includePackages("io.meme.toolbox.wrench")
                              .scan();
        System.out.println();
    }

    @Test
    void ignoreClassVisibility() {
    }

    @Test
    void ignoreFieldVisibility() {
        Result result = Wrench.wrench()
                              .ignoreFieldVisibility()
                              .includePackages("io.meme.toolbox.wrench")
                              .scan();
        System.out.println();
    }

    @Test
    void ignoreVisibilities() {
        Result result = Wrench.wrench()
                              .includePackages("io.meme.toolbox.wrench")
                              .ignoreVisibilities()
                              .scan();
        System.out.println();
    }

    @Test
    void includePackages() {
        Result result = Wrench.wrench()
                              .includePackages("io.meme.toolbox.wrench")
                              .scan();
        System.out.println();
    }

    @Test
    void excludePackages() {
    }

    @Test
    void scan() {
    }
}