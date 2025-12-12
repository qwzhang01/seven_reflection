package io.github.qwzhang01.reflection.test;


import io.github.qwzhang01.reflection.objectmapper.ConverterConfig;
import io.github.qwzhang01.reflection.objectmapper.MapIgnore;
import io.github.qwzhang01.reflection.objectmapper.MapKey;
import io.github.qwzhang01.reflection.objectmapper.ObjectToMapConverter;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 测试类
 */
public class ObjectToMapConverterTest {

    public static void main(String[] args) {
        // 创建测试数据
        User user = createTestUser();

        System.out.println("========== 基本转换测试 ==========");
        ObjectToMapConverter converter = new ObjectToMapConverter();
        Map<String, Object> map = converter.toMap(user);
        printMap(map, 0);

        System.out.println("\n========== 忽略null值测试 ==========");
        ConverterConfig config = new ConverterConfig().setIgnoreNull(true);
        converter = new ObjectToMapConverter(config);
        map = converter.toMap(user);
        printMap(map, 0);

        System.out.println("\n========== 自定义日期转换测试 ==========");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        config = new ConverterConfig().setDateConverter(date -> sdf.format(date));
        converter = new ObjectToMapConverter(config);
        map = converter.toMap(user);
        printMap(map, 0);

        System.out.println("\n========== 自定义类型转换器测试 ==========");
        ObjectToMapConverter.registerConverter(Address.class, address -> {
            Address addr = (Address) address;
            return addr.getCity() + ", " + addr.getCountry();
        });
        map = converter.toMap(user);
        printMap(map, 0);

        System.out.println("\n========== 性能测试 ==========");
        performanceTest();
    }

    private static User createTestUser() {
        User user = new User();
        user.setId(1001L);
        user.setUsername("张三");
        user.setEmail("zhangsan@example.com");
        user.setAge(28);
        user.setActive(true);
        user.setBalance(9999.99);
        user.setRole(Role.ADMIN);
        user.setBirthday(new Date());

        // 地址信息
        Address address = new Address();
        address.setStreet("中关村大街1号");
        address.setCity("北京");
        address.setCountry("中国");
        address.setZipCode("100080");
        user.setAddress(address);

        // 标签列表
        user.setTags(Arrays.asList("Java", "Python", "Go"));

        // 兴趣爱好数组
        user.setHobbies(new String[]{"阅读", "旅游", "编程"});

        // 扩展属性
        Map<String, Object> extra = new HashMap<>();
        extra.put("department", "技术部");
        extra.put("level", 5);
        extra.put("certified", true);
        user.setExtra(extra);

        // 订单列表
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setOrderId("ORD-001");
        order1.setAmount(299.99);
        order1.setStatus(OrderStatus.COMPLETED);
        orders.add(order1);

        Order order2 = new Order();
        order2.setOrderId("ORD-002");
        order2.setAmount(599.99);
        order2.setStatus(OrderStatus.PENDING);
        orders.add(order2);

        user.setOrders(orders);

        return user;
    }

    private static void printMap(Map<String, Object> map, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        String prefix = sb.toString();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                System.out.println(prefix + entry.getKey() + ": {");
                printMap((Map<String, Object>) value, indent + 1);
                System.out.println(prefix + "}");
            } else if (value instanceof List) {
                System.out.println(prefix + entry.getKey() + ": " + value);
            } else {
                System.out.println(prefix + entry.getKey() + ": " + value);
            }
        }
    }

    private static void performanceTest() {
        User user = createTestUser();
        ObjectToMapConverter converter = new ObjectToMapConverter();

        // 预热
        for (int i = 0; i < 1000; i++) {
            converter.toMap(user);
        }

        // 性能测试
        int iterations = 100000;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < iterations; i++) {
            converter.toMap(user);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("总耗时: " + duration + "ms");
        System.out.println("转换次数: " + iterations);
        System.out.println("平均耗时: " + (duration * 1000000.0 / iterations) + "ns");
        System.out.println("QPS: " + (iterations * 1000.0 / duration));
    }

    // ========== 测试用的实体类 ==========

    static class User {
        private Long id;
        private String username;

        @MapKey("user_email")
        private String email;

        private Integer age;
        private Boolean active;
        private Double balance;
        private Role role;
        private Date birthday;

        @MapIgnore
        private String password = "secret123";

        private Address address;
        private List<String> tags;
        private String[] hobbies;
        private Map<String, Object> extra;
        private List<Order> orders;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }

        public Double getBalance() { return balance; }
        public void setBalance(Double balance) { this.balance = balance; }

        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public Date getBirthday() { return birthday; }
        public void setBirthday(Date birthday) { this.birthday = birthday; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }

        public String[] getHobbies() { return hobbies; }
        public void setHobbies(String[] hobbies) { this.hobbies = hobbies; }

        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }

        public List<Order> getOrders() { return orders; }
        public void setOrders(List<Order> orders) { this.orders = orders; }
    }

    static class Address {
        private String street;
        private String city;
        private String country;
        private String zipCode;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }

    static class Order {
        private String orderId;
        private Double amount;
        private OrderStatus status;

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }

    enum Role {
        USER, ADMIN, GUEST
    }

    enum OrderStatus {
        PENDING, COMPLETED, CANCELLED
    }
}
