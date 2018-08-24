package io.meme.toolbox.wrench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceCollector implements Closeable {

    private static final ResourceCollector RESOURCES = new ResourceCollector();

    private List<Closeable> closeables = new CopyOnWriteArrayList<>();

    public static void collect(Closeable closeable) {
        RESOURCES.closeables.add(closeable);
    }

    public static ResourceCollector collector() {
        return RESOURCES;
    }

    @Override
    public void close() {
        closeables.stream()
                  .filter(Objects::nonNull)
                  .forEach(closeable -> {
                   try {
                       closeable.close();
                   }
                   catch (Throwable ignored) {}
               });
        closeables.clear();
    }

}
