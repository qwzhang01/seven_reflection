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
package io.github.qwzhang01.reflection.scanner;

import io.github.qwzhang01.reflection.exception.ReflectionException;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * JAR Package Class Scanner
 * <p>
 * Scans .class files from JAR files.
 * Mainly used for production environment or packaged class file scanning.
 * </p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class JarScanner implements ClassScanner {

    @Override
    public Set<Class<?>> scan(String packageName, Predicate<Class<?>> filter) {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');

        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(packagePath);
            if (url != null && "jar".equals(url.getProtocol())) {
                scanJar(packagePath, url, classes, filter);
            }
        } catch (Exception e) {
            throw new ReflectionException("Failed to scan JAR package: " + packageName, e);
        }

        return classes;
    }

    private void scanJar(String packagePath, URL url,
                         Set<Class<?>> classes, Predicate<Class<?>> filter) {
        try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            JarFile jarFile = connection.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName
                            .substring(0, entryName.length() - 6)
                            .replace('/', '.');

                    try {
                        Class<?> clazz = Class.forName(className);
                        if (filter == null || filter.test(clazz)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        // Ignore classes that cannot be loaded
                    }
                }
            }
        } catch (Exception e) {
            throw new ReflectionException("Failed to scan JAR file", e);
        }
    }
}
