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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Method Accessor - Adopts Facade Design Pattern
 * <p>
 * Provides unified access interface for class methods, supporting method querying and dynamic invocation.
 * All method access is automatically set to accessible (setAccessible), allowing invocation of private methods.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Get all methods of a class (including inherited methods)</li>
 *   <li>Find method by method name and parameter types</li>
 *   <li>Dynamically invoke instance methods and static methods</li>
 *   <li>Filter methods by annotation</li>
 *   <li>Filter methods by return type</li>
 *   <li>Get getter and setter methods</li>
 * </ul>
 *
 * @author avinzhang
 * @since 1.0
 */
public class MethodAccessor {

    private final ReflectionContext context;

    public MethodAccessor() {
        this.context = ReflectionContext.getInstance();
    }

    /**
     * Get all methods
     */
    public List<Method> getAllMethods(Class<?> clazz) {
        ClassMetadata metadata = getOrCreateMetadata(clazz);
        return metadata.getMethods();
    }

    /**
     * Get specified method
     */
    public Optional<Method> getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        return getAllMethods(clazz).stream()
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> Arrays.equals(m.getParameterTypes(), paramTypes))
                .findFirst();
    }

    /**
     * Invoke method
     */
    public Object invoke(Object obj, String methodName, Object... args) {
        Class<?>[] paramTypes = Arrays.stream(args)
                .map(arg -> arg == null ? Object.class : arg.getClass())
                .toArray(Class<?>[]::new);

        return invoke(obj, methodName, paramTypes, args);
    }

    /**
     * Invoke method (specify parameter types)
     */
    public Object invoke(Object obj, String methodName, Class<?>[] paramTypes, Object... args) {
        Method method = getMethod(obj.getClass(), methodName, paramTypes)
                .orElseThrow(() -> new RuntimeException("Method does not exist: " + methodName));

        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException("Failed to invoke method: " + methodName, e);
        }
    }

    /**
     * Invoke static method
     */
    public Object invokeStatic(Class<?> clazz, String methodName,
                               Class<?>[] paramTypes, Object... args) {
        Method method = getMethod(clazz, methodName, paramTypes)
                .orElseThrow(() -> new RuntimeException("Static method does not exist: " + methodName));

        try {
            return method.invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException("Failed to invoke static method: " + methodName, e);
        }
    }

    /**
     * Get methods with specified annotation
     */
    public List<Method> getMethodsWithAnnotation(Class<?> clazz,
                                                 Class<? extends Annotation> annotation) {
        return getAllMethods(clazz).stream()
                .filter(m -> m.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * Get methods of specified return type
     */
    public List<Method> getMethodsByReturnType(Class<?> clazz, Class<?> returnType) {
        return getAllMethods(clazz).stream()
                .filter(m -> m.getReturnType().equals(returnType))
                .collect(Collectors.toList());
    }

    /**
     * Get getter methods
     */
    public List<Method> getGetters(Class<?> clazz) {
        return getAllMethods(clazz).stream()
                .filter(m -> m.getName().startsWith("get") || m.getName().startsWith("is"))
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> !m.getReturnType().equals(void.class))
                .collect(Collectors.toList());
    }

    /**
     * Get setter methods
     */
    public List<Method> getSetters(Class<?> clazz) {
        return getAllMethods(clazz).stream()
                .filter(m -> m.getName().startsWith("set"))
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> m.getReturnType().equals(void.class))
                .collect(Collectors.toList());
    }

    private ClassMetadata getOrCreateMetadata(Class<?> clazz) {
        return context.getMetadataCache()
                .computeIfAbsent(clazz, ClassMetadata::new);
    }
}
