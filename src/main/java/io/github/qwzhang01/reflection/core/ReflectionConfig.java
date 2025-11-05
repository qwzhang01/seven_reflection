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
package io.github.qwzhang01.reflection.core;

/**
 * Reflection Configuration Class
 * <p>
 * Manages global configuration parameters for the reflection toolkit, including cache switch, auto-set accessibility, and other options.
 * Adopts fluent calling method for convenient configuration.
 * </p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ReflectionConfig {

    private boolean cacheEnabled = true;
    private boolean autoSetAccessible = true;
    private int maxCacheSize = 1000;
    private boolean includeInheritedMembers = true;

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public ReflectionConfig setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        return this;
    }

    public boolean isAutoSetAccessible() {
        return autoSetAccessible;
    }

    public ReflectionConfig setAutoSetAccessible(boolean autoSetAccessible) {
        this.autoSetAccessible = autoSetAccessible;
        return this;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public ReflectionConfig setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }

    public boolean isIncludeInheritedMembers() {
        return includeInheritedMembers;
    }

    public ReflectionConfig setIncludeInheritedMembers(boolean includeInheritedMembers) {
        this.includeInheritedMembers = includeInheritedMembers;
        return this;
    }
}
