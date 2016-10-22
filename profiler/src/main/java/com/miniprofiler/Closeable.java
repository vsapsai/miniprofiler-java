package com.miniprofiler;

/**
 * Closeable interface that doesn't throw exceptions.
 */
public interface Closeable extends AutoCloseable {
    void close();
}
