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
package io.github.qwzhang01.reflection.accessor;


import io.github.qwzhang01.reflection.core.ClassMetadata;
import io.github.qwzhang01.reflection.core.ReflectionContext;
import io.github.qwzhang01.reflection.exception.ReflectionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Field Accessor - Adopts Facade Design Pattern
 * <p>
 * Provides unified access interface for class fields, supporting field querying, value getting, and value setting operations.
 * All field access is automatically set to accessible (setAccessible), allowing access to private fields.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Get all fields of a class (including inherited fields)</li>
 *   <li>Find field by field name</li>
 *   <li>Get and set field values (support private fields)</li>
 *   <li>Filter fields by annotation</li>
 *   <li>Filter fields by type</li>
 *   <li>Batch set and get field values</li>
 * </ul>
 *
 * @author avinzhang
 * @since 1.0
 */
public class FieldAccessor {

    private final ReflectionContext context;

    public FieldAccessor() {
        this.context = ReflectionContext.getInstance();
    }

    /**
     * Get all fields
     */
    public List<Field> getAllFields(Class<?> clazz) {
        ClassMetadata metadata = getOrCreateMetadata(clazz);
        return metadata.getFields();
    }

    /**
     * Get specified field
     */
    public Optional<Field> getField(Class<?> clazz, String fieldName) {
        return getAllFields(clazz).stream()
                .filter(f -> f.getName().equals(fieldName))
                .findFirst();
    }

    /**
     * Get field value
     */
    public Object getValue(Object obj, String fieldName) {
        Field field = getField(obj.getClass(), fieldName)
                .orElseThrow(() -> new RuntimeException("Field does not exist: " + fieldName));

        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Failed to get field value: " + fieldName, e);
        }
    }

    /**
     * Set field value
     */
    public void setValue(Object obj, String fieldName, Object value) {
        Field field = getField(obj.getClass(), fieldName)
                .orElseThrow(() -> new RuntimeException("Field does not exist: " + fieldName));

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Failed to set field value: " + fieldName, e);
        }
    }

    /**
     * Get fields with specified annotation
     */
    public List<Field> getFieldsWithAnnotation(Class<?> clazz,
                                               Class<? extends Annotation> annotation) {
        return getAllFields(clazz).stream()
                .filter(f -> f.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Get fields of specified type
     */
    public List<Field> getFieldsByType(Class<?> clazz, Class<?> fieldType) {
        return getAllFields(clazz).stream()
                .filter(f -> f.getType().equals(fieldType))
                .collect(Collectors.toList());
    }

    /**
     * Batch set field values
     */
    public void setValues(Object obj, java.util.Map<String, Object> values) {
        values.forEach((fieldName, value) -> setValue(obj, fieldName, value));
    }

    /**
     * Batch get field values
     */
    public java.util.Map<String, Object> getValues(Object obj, List<String> fieldNames) {
        return fieldNames.stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> getValue(obj, name)
                ));
    }

    private ClassMetadata getOrCreateMetadata(Class<?> clazz) {
        return context.getMetadataCache()
                .computeIfAbsent(clazz, ClassMetadata::new);
    }
}
