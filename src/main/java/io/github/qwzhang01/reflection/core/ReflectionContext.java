/*
 * Copyright 2025 avinzhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.qwzhang01.reflection.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflection Context - Adopts Singleton Design Pattern
 * <p>
 * Manages global cache and configuration, using double-check locking to implement thread-safe singleton.
 * Maintains class cache and metadata cache to improve reflection operation performance.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Class cache: Cache Class objects to avoid repeated loading</li>
 *   <li>Metadata cache: Cache class reflection metadata (fields, methods, constructors)</li>
 *   <li>Global configuration: Manage reflection toolkit configuration parameters</li>
 *   <li>Cache management: Provide cache clearing and statistics functionality</li>
 * </ul>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ReflectionContext {

    private static volatile ReflectionContext instance;

    private final Map<String, Class<?>> classCache;
    private final Map<Class<?>, ClassMetadata> metadataCache;
    private final ReflectionConfig config;

    private ReflectionContext() {
        this.classCache = new ConcurrentHashMap<>(256);
        this.metadataCache = new ConcurrentHashMap<>(256);
        this.config = new ReflectionConfig();
    }

    /**
     * Double-check locking singleton
     */
    public static ReflectionContext getInstance() {
        if (instance == null) {
            synchronized (ReflectionContext.class) {
                if (instance == null) {
                    instance = new ReflectionContext();
                }
            }
        }
        return instance;
    }

    public Map<String, Class<?>> getClassCache() {
        return classCache;
    }

    public Map<Class<?>, ClassMetadata> getMetadataCache() {
        return metadataCache;
    }

    public ReflectionConfig getConfig() {
        return config;
    }

    /**
     * Clear all caches
     */
    public void clearCache() {
        classCache.clear();
        metadataCache.clear();
    }

    /**
     * Get cache statistics
     */
    public CacheStatistics getStatistics() {
        return new CacheStatistics(
                classCache.size(),
                metadataCache.size()
        );
    }

    /**
     * Cache statistics information
     */
    public static class CacheStatistics {
        private final int classCacheSize;
        private final int metadataCacheSize;

        public CacheStatistics(int classCacheSize, int metadataCacheSize) {
            this.classCacheSize = classCacheSize;
            this.metadataCacheSize = metadataCacheSize;
        }

        public int getClassCacheSize() {
            return classCacheSize;
        }

        public int getMetadataCacheSize() {
            return metadataCacheSize;
        }

        @Override
        public String toString() {
            return String.format("CacheStatistics{classes=%d, metadata=%d}",
                    classCacheSize, metadataCacheSize);
        }
    }
}
