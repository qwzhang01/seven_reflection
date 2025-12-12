package io.github.qwzhang01.reflection.objectmapper;

import java.util.Date;
import java.util.function.Function;

/**
 * 转换器配置类
 */
public class ConverterConfig {

    // 是否忽略null值
    private boolean ignoreNull = false;

    // 是否深度转换嵌套对象
    private boolean deepConvert = true;

    // 枚举是否转为字符串
    private boolean enumToString = true;

    // 是否忽略错误继续转换
    private boolean ignoreErrors = false;

    // 日期转换器
    private Function<Date, Object> dateConverter;

    public ConverterConfig() {
    }

    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    public ConverterConfig setIgnoreNull(boolean ignoreNull) {
        this.ignoreNull = ignoreNull;
        return this;
    }

    public boolean isDeepConvert() {
        return deepConvert;
    }

    public ConverterConfig setDeepConvert(boolean deepConvert) {
        this.deepConvert = deepConvert;
        return this;
    }

    public boolean isEnumToString() {
        return enumToString;
    }

    public ConverterConfig setEnumToString(boolean enumToString) {
        this.enumToString = enumToString;
        return this;
    }

    public boolean isIgnoreErrors() {
        return ignoreErrors;
    }

    public ConverterConfig setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
        return this;
    }

    public Function<Date, Object> getDateConverter() {
        return dateConverter;
    }

    public ConverterConfig setDateConverter(Function<Date, Object> dateConverter) {
        this.dateConverter = dateConverter;
        return this;
    }
}
