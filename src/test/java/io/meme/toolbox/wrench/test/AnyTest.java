package io.meme.toolbox.wrench.test;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class AnyTest {

    @Test
    void any() {
        Arrays.stream(this.getClass().getMethods())
              .forEach(method -> method.isDefault());
    }
}