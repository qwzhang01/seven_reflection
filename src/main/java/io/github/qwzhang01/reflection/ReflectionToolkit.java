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
package io.github.qwzhang01.reflection;

import io.github.qwzhang01.reflection.accessor.FieldAccessor;
import io.github.qwzhang01.reflection.accessor.MethodAccessor;
import io.github.qwzhang01.reflection.builder.ObjectBuilder;
import io.github.qwzhang01.reflection.copier.ObjectCopier;
import io.github.qwzhang01.reflection.core.ReflectionContext;
import io.github.qwzhang01.reflection.factory.InstanceFactory;
import io.github.qwzhang01.reflection.mapper.BeanMapper;
import io.github.qwzhang01.reflection.proxy.ReflectionProxy;
import io.github.qwzhang01.reflection.scanner.ClassScanner;
import io.github.qwzhang01.reflection.scanner.CompositeScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Reflection Toolkit Facade Class - Adopts Facade Design Pattern
 * <p>
 * Provides a unified API entry point to simplify reflection operations. Integrates package scanning,
 * instance creation, field/method access, object copying, Bean mapping, builder, and proxy modules.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Package Scanning: Scan classes under specified packages with annotation filtering and inheritance relationship lookup</li>
 *   <li>Instance Creation: Create object instances via reflection, supporting no-arg and parameterized constructors</li>
 *   <li>Field Operations: Get and set field values, support private field access</li>
 *   <li>Method Invocation: Dynamically invoke instance methods and static methods</li>
 *   <li>Object Copying: Support shallow copy and deep copy</li>
 *   <li>Object Mapping: Conversion between objects and Map/JSON</li>
 *   <li>Builder Pattern: Fluent object building</li>
 *   <li>Dynamic Proxy: Create proxy objects with method interception support</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ReflectionToolkit toolkit = ReflectionToolkit.getInstance();
 *
 * // Create instance
 * User user = toolkit.newInstance(User.class);
 *
 * // Set field value
 * toolkit.setFieldValue(user, "name", "John");
 *
 * // Invoke method
 * toolkit.invokeMethod(user, "setAge", 25);
 * }</pre>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ReflectionToolkit {

    private final ClassScanner scanner;
    private final FieldAccessor fieldAccessor;
    private final MethodAccessor methodAccessor;
    private final InstanceFactory instanceFactory;
    private final ObjectCopier objectCopier;
    private final BeanMapper beanMapper;
    private final ReflectionContext context;

    private ReflectionToolkit() {
        this.scanner = new CompositeScanner();
        this.fieldAccessor = new FieldAccessor();
        this.methodAccessor = new MethodAccessor();
        this.instanceFactory = new InstanceFactory();
        this.objectCopier = new ObjectCopier();
        this.beanMapper = new BeanMapper();
        this.context = ReflectionContext.getInstance();
    }

    public static ReflectionToolkit getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Scan all classes under specified package
     *
     * @param packageName package name, e.g. "com.example.demo"
     * @return scanned class set
     */
    public Set<Class<?>> scanPackage(String packageName) {
        return scanner.scan(packageName);
    }

    // ==================== Package Scanning ====================

    /**
     * Scan classes under specified package that match the filter
     *
     * @param packageName package name
     * @param filter      filter for class selection
     * @return matching class set
     */
    public Set<Class<?>> scanPackage(String packageName, Predicate<Class<?>> filter) {
        return scanner.scan(packageName, filter);
    }

    /**
     * Find classes with specific annotation under specified package
     *
     * @param packageName package name
     * @param annotation  annotation type
     * @return class set with specified annotation
     */
    public Set<Class<?>> findClassesWithAnnotation(String packageName,
                                                   Class<? extends Annotation> annotation) {
        return scanner.scan(packageName, clazz -> clazz.isAnnotationPresent(annotation));
    }

    /**
     * Find all subclasses of a class under specified package
     *
     * @param packageName package name
     * @param superClass  parent class
     * @param <T>         generic type
     * @return subclass set
     */
    @SuppressWarnings("unchecked")
    public <T> Set<Class<? extends T>> findSubClasses(String packageName, Class<T> superClass) {
        return (Set<Class<? extends T>>) (Set<?>) scanner.scan(
                packageName,
                clazz -> superClass.isAssignableFrom(clazz) && !clazz.equals(superClass)
        );
    }

    /**
     * Create instance using no-arg constructor
     *
     * @param clazz class object
     * @param <T>   generic type
     * @return newly created instance
     */
    public <T> T newInstance(Class<T> clazz) {
        return instanceFactory.createInstance(clazz);
    }

    // ==================== Instance Creation ====================

    /**
     * Create instance using parameterized constructor (auto-match parameter types)
     *
     * @param clazz class object
     * @param args  constructor parameters
     * @param <T>   generic type
     * @return newly created instance
     */
    public <T> T newInstance(Class<T> clazz, Object... args) {
        return instanceFactory.createInstance(clazz, args);
    }

    /**
     * Create instance using parameterized constructor (specify parameter types)
     *
     * @param clazz      class object
     * @param paramTypes parameter type array
     * @param args       constructor parameters
     * @param <T>        generic type
     * @return newly created instance
     */
    public <T> T newInstance(Class<T> clazz, Class<?>[] paramTypes, Object... args) {
        return instanceFactory.createInstance(clazz, paramTypes, args);
    }

    /**
     * Get all fields of a class (including inherited fields)
     *
     * @param clazz class object
     * @return field list
     */
    public List<Field> getAllFields(Class<?> clazz) {
        return fieldAccessor.getAllFields(clazz);
    }

    // ==================== Field Operations ====================

    /**
     * Get value of specified field from object
     *
     * @param obj       object instance
     * @param fieldName field name
     * @return field value
     */
    public Object getFieldValue(Object obj, String fieldName) {
        return fieldAccessor.getValue(obj, fieldName);
    }

    /**
     * Set value of specified field in object
     *
     * @param obj       object instance
     * @param fieldName field name
     * @param value     value to set
     */
    public void setFieldValue(Object obj, String fieldName, Object value) {
        fieldAccessor.setValue(obj, fieldName, value);
    }

    /**
     * Get all fields with specified annotation in class
     *
     * @param clazz      class object
     * @param annotation annotation type
     * @return field list with specified annotation
     */
    public List<Field> getFieldsWithAnnotation(Class<?> clazz,
                                               Class<? extends Annotation> annotation) {
        return fieldAccessor.getFieldsWithAnnotation(clazz, annotation);
    }

    /**
     * Get all fields of specified type in class
     *
     * @param clazz     class object
     * @param fieldType field type
     * @return field list of specified type
     */
    public List<Field> getFieldsByType(Class<?> clazz, Class<?> fieldType) {
        return fieldAccessor.getFieldsByType(clazz, fieldType);
    }

    /**
     * Get all methods of a class (including inherited methods)
     *
     * @param clazz class object
     * @return method list
     */
    public List<Method> getAllMethods(Class<?> clazz) {
        return methodAccessor.getAllMethods(clazz);
    }

    // ==================== Method Operations ====================

    /**
     * Invoke object method (auto-match parameter types)
     *
     * @param obj        object instance
     * @param methodName method name
     * @param args       method parameters
     * @return method return value
     */
    public Object invokeMethod(Object obj, String methodName, Object... args) {
        return methodAccessor.invoke(obj, methodName, args);
    }

    /**
     * Invoke static method
     *
     * @param clazz      class object
     * @param methodName method name
     * @param paramTypes parameter type array
     * @param args       method parameters
     * @return method return value
     */
    public Object invokeStaticMethod(Class<?> clazz, String methodName,
                                     Class<?>[] paramTypes, Object... args) {
        return methodAccessor.invokeStatic(clazz, methodName, paramTypes, args);
    }

    /**
     * Get all methods with specified annotation in class
     *
     * @param clazz      class object
     * @param annotation annotation type
     * @return method list with specified annotation
     */
    public List<Method> getMethodsWithAnnotation(Class<?> clazz,
                                                 Class<? extends Annotation> annotation) {
        return methodAccessor.getMethodsWithAnnotation(clazz, annotation);
    }

    /**
     * Get all getter methods of class
     *
     * @param clazz class object
     * @return getter method list
     */
    public List<Method> getGetters(Class<?> clazz) {
        return methodAccessor.getGetters(clazz);
    }

    /**
     * Get all setter methods of class
     *
     * @param clazz class object
     * @return setter method list
     */
    public List<Method> getSetters(Class<?> clazz) {
        return methodAccessor.getSetters(clazz);
    }

    /**
     * Shallow copy object
     *
     * @param source source object
     * @param <T>    generic type
     * @return new copied object
     */
    public <T> T shallowCopy(T source) {
        return objectCopier.shallowCopy(source);
    }

    // ==================== Object Copying ====================

    /**
     * Deep copy object (recursively copy all fields)
     *
     * @param source source object
     * @param <T>    generic type
     * @return new copied object
     */
    public <T> T deepCopy(T source) {
        return objectCopier.deepCopy(source);
    }

    /**
     * Copy object properties to target object
     *
     * @param source       source object
     * @param target       target object
     * @param ignoreFields field names to ignore
     */
    public void copyProperties(Object source, Object target, String... ignoreFields) {
        objectCopier.copyProperties(source, target, ignoreFields);
    }

    /**
     * Convert object to Map
     *
     * @param obj object instance
     * @return Map representation of object
     */
    public Map<String, Object> toMap(Object obj) {
        return beanMapper.toMap(obj);
    }

    // ==================== Object Mapping ====================

    /**
     * Create object from Map
     *
     * @param map   Map data
     * @param clazz target type
     * @param <T>   generic type
     * @return created object
     */
    public <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        return beanMapper.fromMap(map, clazz);
    }

    /**
     * Convert object to JSON string (simple implementation)
     *
     * @param obj object instance
     * @return JSON string
     */
    public String toJson(Object obj) {
        return beanMapper.toJson(obj);
    }

    /**
     * Create object from JSON string (simple implementation)
     *
     * @param json  JSON string
     * @param clazz target type
     * @param <T>   generic type
     * @return created object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return beanMapper.fromJson(json, clazz);
    }

    /**
     * Create object builder (from class)
     *
     * @param clazz class object
     * @param <T>   generic type
     * @return object builder
     */
    public <T> ObjectBuilder<T> builder(Class<T> clazz) {
        return ObjectBuilder.of(clazz);
    }

    // ==================== Builder ====================

    /**
     * Create object builder (from instance)
     *
     * @param instance object instance
     * @param <T>      generic type
     * @return object builder
     */
    public <T> ObjectBuilder<T> builder(T instance) {
        return ObjectBuilder.of(instance);
    }

    /**
     * Create proxy object with method interception support
     *
     * @param target       target object
     * @param interceptors method interceptor array
     * @param <T>          generic type
     * @return proxy object
     */
    public <T> T createProxy(T target, ReflectionProxy.MethodInterceptor... interceptors) {
        return ReflectionProxy.createProxy(target, interceptors);
    }

    // ==================== Proxy ====================

    /**
     * Clear all caches
     */
    public void clearCache() {
        context.clearCache();
    }

    // ==================== Cache Management ====================

    /**
     * Get cache statistics
     *
     * @return cache statistics object
     */
    public ReflectionContext.CacheStatistics getCacheStatistics() {
        return context.getStatistics();
    }

    /**
     * Get reflection context object
     *
     * @return reflection context
     */
    public ReflectionContext getContext() {
        return context;
    }

    // ==================== Configuration ====================

    private static class SingletonHolder {
        private static final ReflectionToolkit INSTANCE = new ReflectionToolkit();
    }
}
