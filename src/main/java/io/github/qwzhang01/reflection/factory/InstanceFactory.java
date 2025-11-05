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
package io.github.qwzhang01.reflection.factory;

import io.github.qwzhang01.reflection.core.ClassMetadata;
import io.github.qwzhang01.reflection.core.ReflectionContext;
import io.github.qwzhang01.reflection.exception.ReflectionException;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

/**
 * Instance Factory - Adopts Factory Design Pattern
 * <p>
 * Creates object instances via reflection, supporting both no-arg and parameterized constructors.
 * Automatically finds matching constructor and creates instance.
 * </p>
 *
 * <p>Supported creation methods:</p>
 * <ul>
 *   <li>No-arg constructor creation</li>
 *   <li>Creation with specified parameter types and values</li>
 *   <li>Auto-match parameter types creation</li>
 * </ul>
 *
 * @author avinzhang
 * @since 1.0
 */
public class InstanceFactory {

    private final ReflectionContext context;

    public InstanceFactory() {
        this.context = ReflectionContext.getInstance();
    }

    /**
     * Create instance (no-arg constructor)
     */
    public <T> T createInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new ReflectionException("Failed to create instance: " + clazz.getName(), e);
        }
    }

    /**
     * Create instance (parameterized constructor)
     */
    public <T> T createInstance(Class<T> clazz, Class<?>[] paramTypes, Object... args) {
        Constructor<T> constructor = getConstructor(clazz, paramTypes)
                .orElseThrow(() -> new RuntimeException("Constructor does not exist"));

        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new ReflectionException("Failed to create instance: " + clazz.getName(), e);
        }
    }

    /**
     * Create instance (auto-match constructor)
     */
    public <T> T createInstance(Class<T> clazz, Object... args) {
        Class<?>[] paramTypes = Arrays.stream(args)
                .map(arg -> arg == null ? Object.class : arg.getClass())
                .toArray(Class<?>[]::new);

        return createInstance(clazz, paramTypes, args);
    }

    /**
     * Get constructor
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<Constructor<T>> getConstructor(Class<T> clazz, Class<?>... paramTypes) {
        ClassMetadata metadata = getOrCreateMetadata(clazz);

        return metadata.getConstructors().stream()
                .filter(c -> Arrays.equals(c.getParameterTypes(), paramTypes))
                .map(c -> (Constructor<T>) c)
                .findFirst();
    }

    /**
     * Get all constructors
     */
    @SuppressWarnings("unchecked")
    public <T> Constructor<T>[] getAllConstructors(Class<T> clazz) {
        ClassMetadata metadata = getOrCreateMetadata(clazz);
        return (Constructor<T>[]) metadata.getConstructors().toArray(new Constructor[0]);
    }

    private ClassMetadata getOrCreateMetadata(Class<?> clazz) {
        return context.getMetadataCache()
                .computeIfAbsent(clazz, ClassMetadata::new);
    }
}
