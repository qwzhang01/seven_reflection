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

import java.util.Set;
import java.util.function.Predicate;

/**
 * Class Scanner Interface - Adopts Strategy Design Pattern
 * <p>
 * Defines unified interface for class scanning, different implementations can adopt different scanning strategies.
 * Supports scanning classes from different sources such as file system, JAR packages, etc.
 * </p>
 *
 * @author avinzhang
 * @since 1.0
 */
public interface ClassScanner {

    /**
     * Scan classes
     *
     * @param packageName package name
     * @param filter      filter
     * @return class set
     */
    Set<Class<?>> scan(String packageName, Predicate<Class<?>> filter);

    /**
     * Scan classes (without filter)
     */
    default Set<Class<?>> scan(String packageName) {
        return scan(packageName, null);
    }
}
