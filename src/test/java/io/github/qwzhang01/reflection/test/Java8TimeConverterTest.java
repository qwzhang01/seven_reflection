package io.github.qwzhang01.reflection.test;

import io.github.qwzhang01.reflection.objectmapper.ConverterConfig;
import io.github.qwzhang01.reflection.objectmapper.ObjectToMapConverter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Java 8+ 时间类型转换测试
 */
public class Java8TimeConverterTest {

    public static void main(String[] args) {
        testBasicConversion();
        testCustomConverter();
    }

    /**
     * 测试基本转换（使用默认格式）
     */
    private static void testBasicConversion() {
        System.out.println("========== 基本时间类型转换测试 ==========");
        
        TimeEntity entity = new TimeEntity();
        entity.setId(1L);
        entity.setName("时间测试");
        entity.setLocalDateTime(LocalDateTime.now());
        entity.setLocalDate(LocalDate.now());
        entity.setLocalTime(LocalTime.now());
        entity.setZonedDateTime(ZonedDateTime.now());
        entity.setOffsetDateTime(OffsetDateTime.now());
        entity.setOffsetTime(OffsetTime.now());
        entity.setInstant(Instant.now());
        entity.setDuration(Duration.ofHours(2).plusMinutes(30));
        entity.setPeriod(Period.of(1, 2, 3));
        entity.setYear(Year.of(2024));
        entity.setYearMonth(YearMonth.of(2024, 12));
        entity.setMonthDay(MonthDay.of(12, 25));

        ObjectToMapConverter converter = new ObjectToMapConverter();
        Map<String, Object> result = converter.toMap(entity);

        System.out.println("转换结果：");
        result.forEach((key, value) -> 
            System.out.println("  " + key + ": " + value + " (" + 
                (value != null ? value.getClass().getSimpleName() : "null") + ")")
        );
    }

    /**
     * 测试自定义转换器
     */
    private static void testCustomConverter() {
        System.out.println("\n========== 自定义时间格式转换测试 ==========");
        
        TimeEntity entity = new TimeEntity();
        entity.setId(2L);
        entity.setName("自定义格式测试");
        entity.setLocalDateTime(LocalDateTime.now());
        entity.setLocalDate(LocalDate.now());
        entity.setLocalTime(LocalTime.now());
        entity.setInstant(Instant.now());

        // 配置自定义格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        ConverterConfig config = new ConverterConfig()
                .setLocalDateTimeConverter(dt -> dt.format(dateTimeFormatter))
                .setLocalDateConverter(d -> d.format(dateFormatter))
                .setLocalTimeConverter(t -> t.format(timeFormatter))
                .setInstantConverter(instant -> instant.atZone(ZoneId.systemDefault())
                        .format(dateTimeFormatter));

        ObjectToMapConverter converter = new ObjectToMapConverter(config);
        Map<String, Object> result = converter.toMap(entity);

        System.out.println("转换结果（自定义格式）：");
        result.forEach((key, value) -> 
            System.out.println("  " + key + ": " + value)
        );
    }

    /**
     * 测试实体类
     */
    static class TimeEntity {
        private Long id;
        private String name;
        private LocalDateTime localDateTime;
        private LocalDate localDate;
        private LocalTime localTime;
        private ZonedDateTime zonedDateTime;
        private OffsetDateTime offsetDateTime;
        private OffsetTime offsetTime;
        private Instant instant;
        private Duration duration;
        private Period period;
        private Year year;
        private YearMonth yearMonth;
        private MonthDay monthDay;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public LocalTime getLocalTime() {
            return localTime;
        }

        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }

        public ZonedDateTime getZonedDateTime() {
            return zonedDateTime;
        }

        public void setZonedDateTime(ZonedDateTime zonedDateTime) {
            this.zonedDateTime = zonedDateTime;
        }

        public OffsetDateTime getOffsetDateTime() {
            return offsetDateTime;
        }

        public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
            this.offsetDateTime = offsetDateTime;
        }

        public OffsetTime getOffsetTime() {
            return offsetTime;
        }

        public void setOffsetTime(OffsetTime offsetTime) {
            this.offsetTime = offsetTime;
        }

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }

        public Year getYear() {
            return year;
        }

        public void setYear(Year year) {
            this.year = year;
        }

        public YearMonth getYearMonth() {
            return yearMonth;
        }

        public void setYearMonth(YearMonth yearMonth) {
            this.yearMonth = yearMonth;
        }

        public MonthDay getMonthDay() {
            return monthDay;
        }

        public void setMonthDay(MonthDay monthDay) {
            this.monthDay = monthDay;
        }
    }
}
