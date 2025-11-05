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
package io.github.qwzhang01.reflection.builder;

import io.github.qwzhang01.reflection.accessor.FieldAccessor;
import io.github.qwzhang01.reflection.accessor.MethodAccessor;
import io.github.qwzhang01.reflection.factory.InstanceFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Object Builder - Adopts Builder Design Pattern
 * <p>
 * Provides fluent API for building and configuring objects, making object creation more fluent and readable.
 * Supports chained combination of field setting and method invocation operations.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * User user = ObjectBuilder.of(User.class)
 *     .set("name", "John")
 *     .set("age", 25)
 *     .invoke("setEmail", "john@example.com")
 *     .build();
 * }</pre>
 *
 * @param <T> type of object being built
 * @author avinzhang
 * @since 1.0
 */
public class ObjectBuilder<T> {

    private final T instance;
    private final Class<T> clazz;
    private final FieldAccessor fieldAccessor;
    private final MethodAccessor methodAccessor;

    private ObjectBuilder(T instance, Class<T> clazz) {
        this.instance = instance;
        this.clazz = clazz;
        this.fieldAccessor = new FieldAccessor();
        this.methodAccessor = new MethodAccessor();
    }

    /**
     * Create builder from class
     */
    public static <T> ObjectBuilder<T> of(Class<T> clazz) {
        InstanceFactory factory = new InstanceFactory();
        T instance = factory.createInstance(clazz);
        return new ObjectBuilder<>(instance, clazz);
    }

    /**
     * Create builder from instance
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectBuilder<T> of(T instance) {
        return new ObjectBuilder<>(instance, (Class<T>) instance.getClass());
    }

    /**
     * Create configuration helper
     */
    public static ConfigHelper config() {
        return new ConfigHelper();
    }

    /**
     * Set field value
     */
    public ObjectBuilder<T> set(String fieldName, Object value) {
        fieldAccessor.setValue(instance, fieldName, value);
        return this;
    }

    /**
     * Batch set field values
     */
    public ObjectBuilder<T> setAll(Map<String, Object> values) {
        fieldAccessor.setValues(instance, values);
        return this;
    }

    /**
     * Invoke method
     */
    public ObjectBuilder<T> invoke(String methodName, Object... args) {
        methodAccessor.invoke(instance, methodName, args);
        return this;
    }

    /**
     * Invoke method and get result
     */
    public Object invokeAndGet(String methodName, Object... args) {
        return methodAccessor.invoke(instance, methodName, args);
    }

    /**
     * Get field value
     */
    public Object get(String fieldName) {
        return fieldAccessor.getValue(instance, fieldName);
    }

    /**
     * Build object
     */
    public T build() {
        return instance;
    }

    /**
     * Get type
     */
    public Class<T> getType() {
        return clazz;
    }

    /**
     * Fluent configuration helper
     */
    public static class ConfigHelper {
        private final Map<String, Object> config = new HashMap<>();

        public ConfigHelper add(String key, Object value) {
            config.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return config;
        }
    }
}
