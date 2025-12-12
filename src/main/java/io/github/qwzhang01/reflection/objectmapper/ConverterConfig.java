package io.github.qwzhang01.reflection.objectmapper;

import java.time.*;
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

    // Java 8+ 时间类型转换器
    private Function<LocalDateTime, Object> localDateTimeConverter;
    private Function<LocalDate, Object> localDateConverter;
    private Function<LocalTime, Object> localTimeConverter;
    private Function<ZonedDateTime, Object> zonedDateTimeConverter;
    private Function<OffsetDateTime, Object> offsetDateTimeConverter;
    private Function<OffsetTime, Object> offsetTimeConverter;
    private Function<Instant, Object> instantConverter;
    private Function<Duration, Object> durationConverter;
    private Function<Period, Object> periodConverter;

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

    public Function<LocalDateTime, Object> getLocalDateTimeConverter() {
        return localDateTimeConverter;
    }

    public ConverterConfig setLocalDateTimeConverter(Function<LocalDateTime, Object> localDateTimeConverter) {
        this.localDateTimeConverter = localDateTimeConverter;
        return this;
    }

    public Function<LocalDate, Object> getLocalDateConverter() {
        return localDateConverter;
    }

    public ConverterConfig setLocalDateConverter(Function<LocalDate, Object> localDateConverter) {
        this.localDateConverter = localDateConverter;
        return this;
    }

    public Function<LocalTime, Object> getLocalTimeConverter() {
        return localTimeConverter;
    }

    public ConverterConfig setLocalTimeConverter(Function<LocalTime, Object> localTimeConverter) {
        this.localTimeConverter = localTimeConverter;
        return this;
    }

    public Function<ZonedDateTime, Object> getZonedDateTimeConverter() {
        return zonedDateTimeConverter;
    }

    public ConverterConfig setZonedDateTimeConverter(Function<ZonedDateTime, Object> zonedDateTimeConverter) {
        this.zonedDateTimeConverter = zonedDateTimeConverter;
        return this;
    }

    public Function<OffsetDateTime, Object> getOffsetDateTimeConverter() {
        return offsetDateTimeConverter;
    }

    public ConverterConfig setOffsetDateTimeConverter(Function<OffsetDateTime, Object> offsetDateTimeConverter) {
        this.offsetDateTimeConverter = offsetDateTimeConverter;
        return this;
    }

    public Function<OffsetTime, Object> getOffsetTimeConverter() {
        return offsetTimeConverter;
    }

    public ConverterConfig setOffsetTimeConverter(Function<OffsetTime, Object> offsetTimeConverter) {
        this.offsetTimeConverter = offsetTimeConverter;
        return this;
    }

    public Function<Instant, Object> getInstantConverter() {
        return instantConverter;
    }

    public ConverterConfig setInstantConverter(Function<Instant, Object> instantConverter) {
        this.instantConverter = instantConverter;
        return this;
    }

    public Function<Duration, Object> getDurationConverter() {
        return durationConverter;
    }

    public ConverterConfig setDurationConverter(Function<Duration, Object> durationConverter) {
        this.durationConverter = durationConverter;
        return this;
    }

    public Function<Period, Object> getPeriodConverter() {
        return periodConverter;
    }

    public ConverterConfig setPeriodConverter(Function<Period, Object> periodConverter) {
        this.periodConverter = periodConverter;
        return this;
    }
}
