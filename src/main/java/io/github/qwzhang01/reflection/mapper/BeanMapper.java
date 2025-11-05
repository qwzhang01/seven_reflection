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
package io.github.qwzhang01.reflection.mapper;

import io.github.qwzhang01.reflection.accessor.FieldAccessor;
import io.github.qwzhang01.reflection.factory.InstanceFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bean Mapper - Adopts Adapter Design Pattern
 * <p>
 * Provides conversion functionality between Java objects and Map/JSON.
 * Can be used for object serialization, API data transmission, configuration reading/writing, and other scenarios.
 * </p>
 *
 * <p>Note: JSON conversion is a simple implementation, only supports basic data types and strings.
 * For production environments, it is recommended to use professional JSON libraries (such as Jackson, Gson).</p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class BeanMapper {

    private final FieldAccessor fieldAccessor;
    private final InstanceFactory instanceFactory;

    public BeanMapper() {
        this.fieldAccessor = new FieldAccessor();
        this.instanceFactory = new InstanceFactory();
    }

    /**
     * Convert object to Map
     */
    public Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        List<Field> fields = fieldAccessor.getAllFields(obj.getClass());

        for (Field field : fields) {
            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    Object value = field.get(obj);
                    result.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    // Ignore inaccessible fields
                }
            }
        }

        return result;
    }

    /**
     * Convert Map to object
     */
    public <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        T instance = instanceFactory.createInstance(clazz);

        map.forEach((key, value) -> {
            try {
                fieldAccessor.setValue(instance, key, value);
            } catch (Exception e) {
                // Ignore non-existent fields
            }
        });

        return instance;
    }

    /**
     * Convert object to JSON string (simple implementation)
     */
    public String toJson(Object obj) {
        Map<String, Object> map = toMap(obj);
        return mapToJson(map);
    }

    /**
     * Convert JSON string to object (simple implementation)
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        Map<String, Object> map = jsonToMap(json);
        return fromMap(map, clazz);
    }

    /**
     * Convert object list to Map list
     */
    public List<Map<String, Object>> toMapList(List<?> objects) {
        return objects.stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    /**
     * Convert Map list to object list
     */
    public <T> List<T> fromMapList(List<Map<String, Object>> maps, Class<T> clazz) {
        return maps.stream()
                .map(map -> fromMap(map, clazz))
                .collect(Collectors.toList());
    }

    private String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();

            if (value == null) {
                sb.append("null");
            } else if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(value.toString()).append("\"");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = new LinkedHashMap<>();

        // Simple JSON parsing (only supports basic format)
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);

            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replaceAll("\"", "");
                    String value = kv[1].trim();

                    if (value.equals("null")) {
                        map.put(key, null);
                    } else if (value.startsWith("\"") && value.endsWith("\"")) {
                        map.put(key, value.substring(1, value.length() - 1));
                    } else if (value.equals("true") || value.equals("false")) {
                        map.put(key, Boolean.parseBoolean(value));
                    } else {
                        try {
                            if (value.contains(".")) {
                                map.put(key, Double.parseDouble(value));
                            } else {
                                map.put(key, Integer.parseInt(value));
                            }
                        } catch (NumberFormatException e) {
                            map.put(key, value);
                        }
                    }
                }
            }
        }

        return map;
    }
}
