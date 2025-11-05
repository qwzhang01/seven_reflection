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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Composite Scanner - Adopts Composite Design Pattern
 * <p>
 * Combines multiple class scanners, supports scanning classes from multiple sources simultaneously.
 * Includes file system scanner and JAR scanner by default.
 * </p>
 *
 * <p>When scanning, all registered scanners will be called in sequence and results will be merged.
 * If a scanner fails, other scanners will continue to be used.</p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class CompositeScanner implements ClassScanner {

    private final List<ClassScanner> scanners;

    public CompositeScanner() {
        this.scanners = new ArrayList<>();
        // Add file system and JAR scanners by default
        this.scanners.add(new FileSystemScanner());
        this.scanners.add(new JarScanner());
    }

    public CompositeScanner addScanner(ClassScanner scanner) {
        this.scanners.add(scanner);
        return this;
    }

    @Override
    public Set<Class<?>> scan(String packageName, Predicate<Class<?>> filter) {
        Set<Class<?>> allClasses = new HashSet<>();

        for (ClassScanner scanner : scanners) {
            try {
                Set<Class<?>> classes = scanner.scan(packageName, filter);
                allClasses.addAll(classes);
            } catch (Exception e) {
                // Continue with other scanners
            }
        }

        return allClasses;
    }
}
