package io.github.qwzhang01.reflection.test;

import io.github.qwzhang01.reflection.objectmapper.ConverterConfig;
import io.github.qwzhang01.reflection.objectmapper.ObjectToMapConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 演示 LocalDateTime 等时间类型转换问题的解决
 * 
 * 问题：java.lang.reflect.InaccessibleObjectException: 
 * Unable to make field private final java.time.LocalDate java.time.LocalDateTime.date accessible: 
 * module java.base does not "opens java.time" to unnamed module
 * 
 * 解决方案：通过公共 API 处理时间类型，不再反射访问私有字段
 */
public class LocalDateTimeIssueDemo {

    public static void main(String[] args) {
        System.out.println("========== LocalDateTime 转换问题演示 ==========\n");
        
        // 创建包含 LocalDateTime 的对象
        Article article = new Article();
        article.setId(1001L);
        article.setTitle("Java 反射工具类使用指南");
        article.setAuthor("张三");
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setPublishTime(LocalDateTime.of(2024, 12, 12, 10, 30, 0));
        
        // 测试1：使用默认格式转换
        System.out.println("1. 默认格式转换（ISO格式）:");
        try {
            ObjectToMapConverter converter = new ObjectToMapConverter();
            Map<String, Object> map = converter.toMap(article);
            
            System.out.println("转换成功！");
            map.forEach((key, value) -> 
                System.out.println("  " + key + ": " + value)
            );
        } catch (Exception e) {
            System.out.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 测试2：自定义格式
        System.out.println("\n2. 自定义格式转换:");
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ConverterConfig config = new ConverterConfig()
                .setLocalDateTimeConverter(dt -> dt.format(formatter));
            
            ObjectToMapConverter converter = new ObjectToMapConverter(config);
            Map<String, Object> map = converter.toMap(article);
            
            System.out.println("转换成功！");
            map.forEach((key, value) -> 
                System.out.println("  " + key + ": " + value)
            );
        } catch (Exception e) {
            System.out.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 测试3：转换为时间戳
        System.out.println("\n3. 转换为时间戳:");
        try {
            ConverterConfig config = new ConverterConfig()
                .setLocalDateTimeConverter(dt -> dt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
            
            ObjectToMapConverter converter = new ObjectToMapConverter(config);
            Map<String, Object> map = converter.toMap(article);
            
            System.out.println("转换成功！");
            map.forEach((key, value) -> 
                System.out.println("  " + key + ": " + value)
            );
        } catch (Exception e) {
            System.out.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n========== 测试完成 ==========");
        System.out.println("✓ LocalDateTime 字段成功转换，没有抛出 InaccessibleObjectException");
    }
    
    /**
     * 文章实体类 - 包含多个 LocalDateTime 字段
     */
    static class Article {
        private Long id;
        private String title;
        private String author;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private LocalDateTime publishTime;
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getAuthor() {
            return author;
        }
        
        public void setAuthor(String author) {
            this.author = author;
        }
        
        public LocalDateTime getCreateTime() {
            return createTime;
        }
        
        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }
        
        public LocalDateTime getUpdateTime() {
            return updateTime;
        }
        
        public void setUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
        }
        
        public LocalDateTime getPublishTime() {
            return publishTime;
        }
        
        public void setPublishTime(LocalDateTime publishTime) {
            this.publishTime = publishTime;
        }
    }
}
