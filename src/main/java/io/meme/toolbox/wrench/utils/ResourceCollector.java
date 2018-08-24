package io.meme.toolbox.wrench.utils;

import lombok.NoArgsConstructor;

import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author meme
 * @since 1.0
 */
@NoArgsConstructor(staticName = "collector")
public class ResourceCollector implements Closeable {

    private List<Closeable> closeables = new CopyOnWriteArrayList<>();

    public void collect(Closeable closeable) {
        closeables.add(closeable);
    }

    @Override
    public void close() {
        closeables.stream()
                  .filter(Objects::nonNull)
                  .forEach(this::close);
        closeables.clear();
    }

    private void close(Closeable closeable) {
        try {
            closeable.close();
        }
        catch (Throwable ignored) {}
    }

}
