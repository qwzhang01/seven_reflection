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
package io.github.qwzhang01.reflection.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection Proxy - Adopts Proxy Design Pattern and Chain of Responsibility Pattern
 * <p>
 * Implements method interception functionality based on JDK dynamic proxy, supporting AOP programming.
 * Provides interceptor chain mechanism to combine multiple interceptors.
 * </p>
 *
 * <p>Built-in interceptors:</p>
 * <ul>
 *   <li>LoggingInterceptor: Logging interceptor, records method invocation information</li>
 *   <li>PerformanceInterceptor: Performance monitoring interceptor, monitors method execution time</li>
 *   <li>CacheInterceptor: Cache interceptor, caches method return values</li>
 * </ul>
 *
 * <p>Note: Only supports interface proxy, target object must implement at least one interface</p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ReflectionProxy {

    /**
     * Create proxy object
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> interfaceClass, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                handler
        );
    }

    /**
     * Create proxy object (support multiple interfaces)
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(ClassLoader classLoader,
                                    Class<?>[] interfaces,
                                    InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
    }

    /**
     * Create proxy with interceptors
     */
    public static <T> T createProxy(T target, MethodInterceptor... interceptors) {
        return createProxy(target, new InterceptorChain(interceptors));
    }

    /**
     * Create proxy with interceptors
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, InterceptorChain chain) {
        Class<?> targetClass = target.getClass();
        Class<?>[] interfaces = targetClass.getInterfaces();

        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Target object must implement at least one interface");
        }

        return (T) Proxy.newProxyInstance(
                targetClass.getClassLoader(),
                interfaces,
                (proxy, method, args) -> chain.proceed(target, method, args)
        );
    }

    /**
     * Method interceptor interface
     */
    public interface MethodInterceptor {
        Object intercept(Object target, Method method, Object[] args, InterceptorChain chain)
                throws Throwable;
    }

    /**
     * Interceptor chain
     */
    public static class InterceptorChain {
        private final List<MethodInterceptor> interceptors;
        private int currentIndex = 0;

        public InterceptorChain(MethodInterceptor... interceptors) {
            this.interceptors = new ArrayList<>();
            for (MethodInterceptor interceptor : interceptors) {
                this.interceptors.add(interceptor);
            }
        }

        public Object proceed(Object target, Method method, Object[] args) throws Throwable {
            if (currentIndex < interceptors.size()) {
                MethodInterceptor interceptor = interceptors.get(currentIndex++);
                return interceptor.intercept(target, method, args, this);
            } else {
                // Execute original method
                return method.invoke(target, args);
            }
        }
    }

    /**
     * Logging interceptor
     */
    public static class LoggingInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object target, Method method, Object[] args, InterceptorChain chain)
                throws Throwable {
            System.out.println("Invoking method: " + method.getName());
            long start = System.nanoTime();

            try {
                Object result = chain.proceed(target, method, args);
                long end = System.nanoTime();
                System.out.println("Method execution completed, time taken: " + (end - start) / 1000 + " μs");
                return result;
            } catch (Throwable e) {
                System.out.println("Method execution exception: " + e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Performance monitoring interceptor
     */
    public static class PerformanceInterceptor implements MethodInterceptor {
        private final long threshold;

        public PerformanceInterceptor(long thresholdNanos) {
            this.threshold = thresholdNanos;
        }

        @Override
        public Object intercept(Object target, Method method, Object[] args, InterceptorChain chain)
                throws Throwable {
            long start = System.nanoTime();
            Object result = chain.proceed(target, method, args);
            long duration = System.nanoTime() - start;

            if (duration > threshold) {
                System.out.println(String.format(
                        "Warning: Method %s execution time %d ns exceeds threshold %d ns",
                        method.getName(), duration, threshold
                ));
            }

            return result;
        }
    }

    /**
     * Cache interceptor
     */
    public static class CacheInterceptor implements MethodInterceptor {
        private final java.util.Map<String, Object> cache = new java.util.concurrent.ConcurrentHashMap<>();

        @Override
        public Object intercept(Object target, Method method, Object[] args, InterceptorChain chain)
                throws Throwable {
            String key = buildCacheKey(method, args);

            if (cache.containsKey(key)) {
                System.out.println("Returning from cache: " + method.getName());
                return cache.get(key);
            }

            Object result = chain.proceed(target, method, args);
            cache.put(key, result);
            return result;
        }

        private String buildCacheKey(Method method, Object[] args) {
            StringBuilder sb = new StringBuilder(method.getName());
            if (args != null) {
                for (Object arg : args) {
                    sb.append("_").append(arg);
                }
            }
            return sb.toString();
        }
    }
}
