package io.meme.joke.classscanner;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClassScannerTest {

    @Test
    public void scan() {
        Result scan = ClassScanner.allPackages().ignoreVisibility().scan();
    }
}