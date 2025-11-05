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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class Metadata - Adopts Flyweight Design Pattern
 * <p>
 * Caches class reflection information (fields, methods, constructors) to avoid repeated reflection calls and improve performance.
 * Uses lazy loading and double-check locking mechanism to collect class metadata only when needed.
 * </p>
 *
 * <p>Cached metadata includes:</p>
 * <ul>
 *   <li>All fields (including inherited fields)</li>
 *   <li>All methods (including inherited methods)</li>
 *   <li>All constructors</li>
 * </ul>
 *
 * <p>Note: Collected fields and methods are automatically set to accessible (setAccessible(true))</p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ClassMetadata {

    private final Class<?> targetClass;
    private List<Field> fields;
    private List<Method> methods;
    private List<Constructor<?>> constructors;

    public ClassMetadata(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * Lazy load field list
     */
    public List<Field> getFields() {
        if (fields == null) {
            synchronized (this) {
                if (fields == null) {
                    fields = collectFields();
                }
            }
        }
        return fields;
    }

    /**
     * Lazy load method list
     */
    public List<Method> getMethods() {
        if (methods == null) {
            synchronized (this) {
                if (methods == null) {
                    methods = collectMethods();
                }
            }
        }
        return methods;
    }

    /**
     * Lazy load constructor list
     */
    public List<Constructor<?>> getConstructors() {
        if (constructors == null) {
            synchronized (this) {
                if (constructors == null) {
                    constructors = collectConstructors();
                }
            }
        }
        return constructors;
    }

    private List<Field> collectFields() {
        List<Field> result = new ArrayList<>();
        Class<?> current = targetClass;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                field.setAccessible(true);
                result.add(field);
            }
            current = current.getSuperclass();
        }

        return Collections.unmodifiableList(result);
    }

    private List<Method> collectMethods() {
        List<Method> result = new ArrayList<>();
        Class<?> current = targetClass;

        while (current != null && current != Object.class) {
            for (Method method : current.getDeclaredMethods()) {
                method.setAccessible(true);
                result.add(method);
            }
            current = current.getSuperclass();
        }

        return Collections.unmodifiableList(result);
    }

    private List<Constructor<?>> collectConstructors() {
        List<Constructor<?>> result = new ArrayList<>();
        for (Constructor<?> constructor : targetClass.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            result.add(constructor);
        }
        return Collections.unmodifiableList(result);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
