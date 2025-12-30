package io.github.qwzhang01.reflection.objectmapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高性能对象转Map工具
 * 特性：
 * 1. 反射结果缓存，提升性能
 * 2. 支持嵌套对象递归转换
 * 3. 支持集合、数组处理
 * 4. 支持自定义转换器
 * 5. 支持注解配置
 */
public class ObjectToMapConverter {

    // 缓存类的字段信息，避免重复反射
    private static final Map<Class<?>, List<FieldAccessor>> FIELD_CACHE = new ConcurrentHashMap<>();

    // 自定义类型转换器
    private static final Map<Class<?>, TypeConverter<?>> TYPE_CONVERTERS = new ConcurrentHashMap<>();

    // 配置选项
    private final ConverterConfig config;

    public ObjectToMapConverter() {
        this(new ConverterConfig());
    }

    public ObjectToMapConverter(ConverterConfig config) {
        this.config = config;
    }

    /**
     * 将对象转换为Map
     */
    public Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return null;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        convert(obj, result, new HashSet<>());
        return result;
    }

    /**
     * 核心转换逻辑
     */
    private void convert(Object obj, Map<String, Object> result, Set<Object> visited) {
        if (obj == null) {
            return;
        }

        // 防止循环引用
        if (visited.contains(obj)) {
            return;
        }
        visited.add(obj);

        Class<?> clazz = obj.getClass();

        // 获取缓存的字段访问器
        List<FieldAccessor> accessors = FIELD_CACHE.computeIfAbsent(clazz, this::buildFieldAccessors);

        for (FieldAccessor accessor : accessors) {
            try {
                Object value = accessor.getValue(obj);

                if (value == null && config.isIgnoreNull()) {
                    continue;
                }

                String key = accessor.getName();
                Object convertedValue = convertValue(value, visited);
                result.put(key, convertedValue);

            } catch (Exception e) {
                if (!config.isIgnoreErrors()) {
                    throw new RuntimeException("Failed to convert field: " + accessor.getName(), e);
                }
            }
        }
    }

    /**
     * 转换值，处理各种类型
     */
    private Object convertValue(Object value, Set<Object> visited) {
        if (value == null) {
            return null;
        }

        Class<?> valueClass = value.getClass();

        // 基本类型和包装类
        if (isPrimitiveOrWrapper(valueClass)) {
            return value;
        }

        // 字符串
        if (value instanceof String) {
            return value;
        }

        // BigDecimal 和 BigInteger - 必须在反射处理之前，避免模块访问问题
        if (value instanceof BigDecimal || value instanceof BigInteger) {
            return value;
        }

        // Java 8+ 时间类型 - 必须在 Date 之前处理，避免反射访问私有字段
        if (value instanceof LocalDateTime) {
            return config.getLocalDateTimeConverter() != null ?
                    config.getLocalDateTimeConverter().apply((LocalDateTime) value) :
                    ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        if (value instanceof LocalDate) {
            return config.getLocalDateConverter() != null ?
                    config.getLocalDateConverter().apply((LocalDate) value) :
                    ((LocalDate) value).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        if (value instanceof LocalTime) {
            return config.getLocalTimeConverter() != null ?
                    config.getLocalTimeConverter().apply((LocalTime) value) :
                    ((LocalTime) value).format(DateTimeFormatter.ISO_LOCAL_TIME);
        }

        if (value instanceof ZonedDateTime) {
            return config.getZonedDateTimeConverter() != null ?
                    config.getZonedDateTimeConverter().apply((ZonedDateTime) value) :
                    ((ZonedDateTime) value).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        }

        if (value instanceof OffsetDateTime) {
            return config.getOffsetDateTimeConverter() != null ?
                    config.getOffsetDateTimeConverter().apply((OffsetDateTime) value) :
                    ((OffsetDateTime) value).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        if (value instanceof OffsetTime) {
            return config.getOffsetTimeConverter() != null ?
                    config.getOffsetTimeConverter().apply((OffsetTime) value) :
                    ((OffsetTime) value).format(DateTimeFormatter.ISO_OFFSET_TIME);
        }

        if (value instanceof Instant) {
            return config.getInstantConverter() != null ?
                    config.getInstantConverter().apply((Instant) value) :
                    ((Instant) value).toString();
        }

        if (value instanceof Duration) {
            return config.getDurationConverter() != null ?
                    config.getDurationConverter().apply((Duration) value) :
                    ((Duration) value).toString();
        }

        if (value instanceof Period) {
            return config.getPeriodConverter() != null ?
                    config.getPeriodConverter().apply((Period) value) :
                    ((Period) value).toString();
        }

        if (value instanceof Year) {
            return ((Year) value).getValue();
        }

        if (value instanceof YearMonth) {
            return ((YearMonth) value).toString();
        }

        if (value instanceof MonthDay) {
            return ((MonthDay) value).toString();
        }

        // 日期类型
        if (value instanceof Date) {
            return config.getDateConverter() != null ?
                    config.getDateConverter().apply((Date) value) : value;
        }

        // 枚举
        if (value instanceof Enum) {
            return config.isEnumToString() ? ((Enum<?>) value).name() : value;
        }

        // 自定义转换器
        @SuppressWarnings("unchecked")
        TypeConverter<Object> converter = (TypeConverter<Object>) TYPE_CONVERTERS.get(valueClass);
        if (converter != null) {
            return converter.convert(value);
        }

        // 集合类型
        if (value instanceof Collection) {
            return convertCollection((Collection<?>) value, visited);
        }

        // 数组类型
        if (valueClass.isArray()) {
            return convertArray(value, visited);
        }

        // Map类型
        if (value instanceof Map) {
            return convertMap((Map<?, ?>) value, visited);
        }

        // 复杂对象，递归转换
        if (config.isDeepConvert()) {
            Map<String, Object> nestedMap = new LinkedHashMap<>();
            convert(value, nestedMap, visited);
            return nestedMap;
        }

        return value.toString();
    }

    /**
     * 转换集合
     */
    private List<Object> convertCollection(Collection<?> collection, Set<Object> visited) {
        List<Object> result = new ArrayList<>(collection.size());
        for (Object item : collection) {
            result.add(convertValue(item, visited));
        }
        return result;
    }

    /**
     * 转换数组
     */
    private List<Object> convertArray(Object array, Set<Object> visited) {
        int length = java.lang.reflect.Array.getLength(array);
        List<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);
            result.add(convertValue(item, visited));
        }
        return result;
    }

    /**
     * 转换Map
     */
    private Map<String, Object> convertMap(Map<?, ?> map, Set<Object> visited) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey().toString() : null;
            if (key != null) {
                result.put(key, convertValue(entry.getValue(), visited));
            }
        }
        return result;
    }

    /**
     * 构建字段访问器列表
     */
    private List<FieldAccessor> buildFieldAccessors(Class<?> clazz) {
        List<FieldAccessor> accessors = new ArrayList<>();

        // 遍历所有字段（包括父类）
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                // 跳过静态字段和transient字段
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                    continue;
                }

                // 检查是否有@Ignore注解
                if (field.isAnnotationPresent(MapIgnore.class)) {
                    continue;
                }

                // 获取字段名称（支持@MapKey注解自定义）
                String fieldName = field.getName();
                if (field.isAnnotationPresent(MapKey.class)) {
                    MapKey mapKey = field.getAnnotation(MapKey.class);
                    if (!mapKey.value().isEmpty()) {
                        fieldName = mapKey.value();
                    }
                }

                // 尝试使用getter方法
                FieldAccessor accessor = createAccessor(field, fieldName, currentClass);
                if (accessor != null) {
                    accessors.add(accessor);
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return accessors;
    }

    /**
     * 创建字段访问器
     */
    private FieldAccessor createAccessor(Field field, String name, Class<?> clazz) {
        // 优先使用getter方法
        Method getter = findGetter(field, clazz);
        if (getter != null) {
            return new MethodAccessor(name, getter);
        }

        // 直接访问字段
        try {
            field.setAccessible(true);
            return new DirectFieldAccessor(name, field);
        } catch (Exception e) {
            // Java 9+ 模块系统可能阻止访问某些字段
            // 对于无法访问的字段，返回null，在buildFieldAccessors中会被过滤
            if (config.isIgnoreErrors()) {
                return null;
            }
            throw new RuntimeException("Cannot access field: " + name, e);
        }
    }

    /**
     * 查找getter方法
     */
    private Method findGetter(Field field, Class<?> clazz) {
        String fieldName = field.getName();
        String getterName = "get" + capitalize(fieldName);

        // 处理boolean类型的is前缀
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            String isGetterName = "is" + capitalize(fieldName);
            try {
                Method method = clazz.getMethod(isGetterName);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                // 继续尝试get前缀
            }
        }

        try {
            Method method = clazz.getMethod(getterName);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 判断是否为基本类型或包装类
     */
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class ||
                clazz == Byte.class ||
                clazz == Character.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == BigDecimal.class ||
                clazz == BigInteger.class;
    }

    /**
     * 首字母大写
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 注册自定义类型转换器
     */
    public static <T> void registerConverter(Class<T> clazz, TypeConverter<T> converter) {
        TYPE_CONVERTERS.put(clazz, converter);
    }

    /**
     * 清除缓存（主要用于测试）
     */
    public static void clearCache() {
        FIELD_CACHE.clear();
    }

    // ========== 内部类 ==========

    /**
     * 字段访问器接口
     */
    interface FieldAccessor {
        String getName();
        Object getValue(Object obj) throws Exception;
    }

    /**
     * 通过Method访问字段
     */
    static class MethodAccessor implements FieldAccessor {
        private final String name;
        private final Method method;

        MethodAccessor(String name, Method method) {
            this.name = name;
            this.method = method;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getValue(Object obj) throws Exception {
            return method.invoke(obj);
        }
    }

    /**
     * 直接访问字段
     */
    static class DirectFieldAccessor implements FieldAccessor {
        private final String name;
        private final Field field;

        DirectFieldAccessor(String name, Field field) {
            this.name = name;
            this.field = field;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getValue(Object obj) throws Exception {
            return field.get(obj);
        }
    }
}
