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

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * File System Class Scanner
 * <p>
 * Scans .class files from file system, supports recursive scanning of subdirectories.
 * Mainly used for development environment or extracted class file scanning.
 * </p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class FileSystemScanner implements ClassScanner {

    @Override
    public Set<Class<?>> scan(String packageName, Predicate<Class<?>> filter) {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');

        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(packagePath);
            if (url != null && "file".equals(url.getProtocol())) {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                scanDirectory(packageName, filePath, classes, filter);
            }
        } catch (Exception e) {
            throw new ReflectionException("Failed to scan package: " + packageName, e);
        }

        return classes;
    }

    private void scanDirectory(String packageName, String packagePath,
                               Set<Class<?>> classes, Predicate<Class<?>> filter) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles(file ->
                file.isDirectory() || file.getName().endsWith(".class")
        );

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(
                        packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        classes,
                        filter
                );
            } else {
                String className = packageName + "." +
                        file.getName().substring(0, file.getName().length() - 6);
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
    }
}
