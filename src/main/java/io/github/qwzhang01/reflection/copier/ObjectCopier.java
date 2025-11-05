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
package io.github.qwzhang01.reflection.copier;

import io.github.qwzhang01.reflection.accessor.FieldAccessor;
import io.github.qwzhang01.reflection.exception.ReflectionException;
import io.github.qwzhang01.reflection.factory.InstanceFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Object Copier - Adopts Prototype Design Pattern
 * <p>
 * Provides object copying functionality, supporting shallow copy, deep copy, and property copy.
 * Can be used for object cloning, Data Transfer Object (DTO) conversion, and other scenarios.
 * </p>
 *
 * <p>Copy strategies:</p>
 * <ul>
 *   <li>Shallow copy: Only copies the object itself, field references remain unchanged</li>
 *   <li>Deep copy: Recursively copies object and all its fields (simple implementation, does not handle circular references)</li>
 *   <li>Property copy: Copies fields with same name and type between two objects</li>
 * </ul>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ObjectCopier {

    private final FieldAccessor fieldAccessor;
    private final InstanceFactory instanceFactory;

    public ObjectCopier() {
        this.fieldAccessor = new FieldAccessor();
        this.instanceFactory = new InstanceFactory();
    }

    /**
     * Shallow copy
     */
    @SuppressWarnings("unchecked")
    public <T> T shallowCopy(T source) {
        if (source == null) {
            return null;
        }

        Class<T> clazz = (Class<T>) source.getClass();
        T target = instanceFactory.createInstance(clazz);

        List<Field> fields = fieldAccessor.getAllFields(clazz);
        for (Field field : fields) {
            try {
                Object value = field.get(source);
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Failed to copy field: " + field.getName(), e);
            }
        }

        return target;
    }

    /**
     * Deep copy (simple implementation, does not handle circular references)
     */
    @SuppressWarnings("unchecked")
    public <T> T deepCopy(T source) {
        if (source == null) {
            return null;
        }

        Class<T> clazz = (Class<T>) source.getClass();

        // Return directly for primitive and wrapper types
        if (isPrimitiveOrWrapper(clazz) || clazz == String.class) {
            return source;
        }

        T target = instanceFactory.createInstance(clazz);

        List<Field> fields = fieldAccessor.getAllFields(clazz);
        for (Field field : fields) {
            try {
                Object value = field.get(source);
                if (value != null) {
                    if (isPrimitiveOrWrapper(value.getClass()) || value instanceof String) {
                        field.set(target, value);
                    } else {
                        // Recursively deep copy
                        Object copiedValue = deepCopy(value);
                        field.set(target, copiedValue);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Failed to deep copy field: " + field.getName(), e);
            }
        }

        return target;
    }

    /**
     * Copy properties
     */
    public void copyProperties(Object source, Object target, String... ignoreFields) {
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreFields));

        List<Field> sourceFields = fieldAccessor.getAllFields(source.getClass());
        List<Field> targetFields = fieldAccessor.getAllFields(target.getClass());

        for (Field sourceField : sourceFields) {
            if (ignoreSet.contains(sourceField.getName())) {
                continue;
            }

            for (Field targetField : targetFields) {
                if (sourceField.getName().equals(targetField.getName()) &&
                        sourceField.getType().equals(targetField.getType())) {
                    try {
                        Object value = sourceField.get(source);
                        targetField.set(target, value);
                    } catch (IllegalAccessException e) {
                        // Ignore inaccessible fields
                    }
                    break;
                }
            }
        }
    }

    /**
     * Selective copy
     */
    public void copyProperties(Object source, Object target,
                               CopyStrategy strategy, String... fields) {
        Set<String> fieldSet = new HashSet<>(Arrays.asList(fields));

        List<Field> sourceFields = fieldAccessor.getAllFields(source.getClass());
        List<Field> targetFields = fieldAccessor.getAllFields(target.getClass());

        for (Field sourceField : sourceFields) {
            boolean shouldCopy = strategy == CopyStrategy.INCLUDE
                    ? fieldSet.contains(sourceField.getName())
                    : !fieldSet.contains(sourceField.getName());

            if (!shouldCopy) {
                continue;
            }

            for (Field targetField : targetFields) {
                if (sourceField.getName().equals(targetField.getName()) &&
                        sourceField.getType().equals(targetField.getType())) {
                    try {
                        Object value = sourceField.get(source);
                        targetField.set(target, value);
                    } catch (IllegalAccessException e) {
                        // Ignore inaccessible fields
                    }
                    break;
                }
            }
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class || clazz == Byte.class ||
                clazz == Character.class || clazz == Short.class ||
                clazz == Integer.class || clazz == Long.class ||
                clazz == Float.class || clazz == Double.class;
    }

    /**
     * Copy strategy
     */
    public enum CopyStrategy {
        INCLUDE,  // Include specified fields
        EXCLUDE   // Exclude specified fields
    }
}
