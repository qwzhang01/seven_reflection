package io.github.qwzhang01.reflection.objectmapper;
/**
 * 自定义类型转换器接口
 */
@FunctionalInterface
public interface TypeConverter<T> {

    /**
     * 将对象转换为目标类型
     * @param value 原始值
     * @return 转换后的值
     */
    Object convert(T value);
}
