package io.github.qwzhang01.reflection.test;


import io.github.qwzhang01.reflection.objectmapper.ConverterConfig;
import io.github.qwzhang01.reflection.objectmapper.MapIgnore;
import io.github.qwzhang01.reflection.objectmapper.MapKey;
import io.github.qwzhang01.reflection.objectmapper.ObjectToMapConverter;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 快速开始示例
 */
public class QuickStartExample {

    public static void main(String[] args) {
        String separator = repeatString("=", 50);

        example1_BasicUsage();
        System.out.println("\n" + separator + "\n");

        example2_NestedObject();
        System.out.println("\n" + separator + "\n");

        example3_WithAnnotations();
        System.out.println("\n" + separator + "\n");

        example4_CustomConverter();
    }

    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 示例1：基本用法
     */
    private static void example1_BasicUsage() {
        System.out.println("示例1：基本用法");
        System.out.println("---");

        Person person = new Person();
        person.setName("张三");
        person.setAge(25);
        person.setEmail("zhangsan@example.com");

        ObjectToMapConverter converter = new ObjectToMapConverter();
        Map<String, Object> map = converter.toMap(person);

        System.out.println("转换结果：");
        map.forEach((k, v) -> System.out.println("  " + k + ": " + v));
    }

    /**
     * 示例2：嵌套对象
     */
    private static void example2_NestedObject() {
        System.out.println("示例2：嵌套对象转换");
        System.out.println("---");

        Company company = new Company();
        company.setName("科技有限公司");
        company.setEmployeeCount(100);

        Address address = new Address();
        address.setCity("北京");
        address.setStreet("中关村大街");
        company.setAddress(address);

        ObjectToMapConverter converter = new ObjectToMapConverter();
        Map<String, Object> map = converter.toMap(company);

        System.out.println("转换结果：");
        printMap(map, 1);
    }

    /**
     * 示例3：使用注解
     */
    private static void example3_WithAnnotations() {
        System.out.println("示例3：使用注解");
        System.out.println("---");

        Account account = new Account();
        account.setAccountId(12345L);
        account.setUsername("李四");
        account.setPassword("secret123");  // 会被@MapIgnore忽略

        ObjectToMapConverter converter = new ObjectToMapConverter();
        Map<String, Object> map = converter.toMap(account);

        System.out.println("转换结果：");
        map.forEach((k, v) -> System.out.println("  " + k + ": " + v));
        System.out.println("\n注意：password字段被@MapIgnore忽略了");
    }

    /**
     * 示例4：自定义转换器
     */
    private static void example4_CustomConverter() {
        System.out.println("示例4：自定义转换器");
        System.out.println("---");

        Employee employee = new Employee();
        employee.setName("王五");
        employee.setHireDate(new Date());

        // 自定义日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        ConverterConfig config = new ConverterConfig()
                .setDateConverter(date -> sdf.format(date));

        ObjectToMapConverter converter = new ObjectToMapConverter(config);
        Map<String, Object> map = converter.toMap(employee);

        System.out.println("转换结果：");
        map.forEach((k, v) -> System.out.println("  " + k + ": " + v));
    }

    private static void printMap(Map<String, Object> map, int indent) {
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            prefix.append("  ");
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                System.out.println(prefix + entry.getKey() + ": {");
                printMap((Map<String, Object>) entry.getValue(), indent + 1);
                System.out.println(prefix + "}");
            } else {
                System.out.println(prefix + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // ========== 测试实体类 ==========

    static class Person {
        private String name;
        private Integer age;
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    static class Company {
        private String name;
        private Integer employeeCount;
        private Address address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    static class Address {
        private String city;
        private String street;

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
    }

    static class Account {
        @MapKey("account_id")
        private Long accountId;

        private String username;

        @MapIgnore
        private String password;

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class Employee {
        private String name;
        private Date hireDate;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Date getHireDate() { return hireDate; }
        public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
    }
}
