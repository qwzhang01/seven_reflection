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
package io.github.qwzhang01.reflection.demo;


import io.github.qwzhang01.reflection.ReflectionToolkit;
import io.github.qwzhang01.reflection.builder.ObjectBuilder;
import io.github.qwzhang01.reflection.demo.model.*;
import io.github.qwzhang01.reflection.demo.service.UserService;
import io.github.qwzhang01.reflection.demo.service.UserServiceImpl;
import io.github.qwzhang01.reflection.proxy.ReflectionProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 反射工具包演示程序
 * <p>
 * 全面演示反射工具包的各种功能，包括：
 * </p>
 * <ul>
 *   <li>包扫描：扫描指定包下的类，支持注解和继承关系查找</li>
 *   <li>实例创建：使用无参和有参构造器创建对象</li>
 *   <li>字段操作：读写字段值，包括私有字段</li>
 *   <li>方法调用：动态调用getter/setter等方法</li>
 *   <li>对象复制：浅拷贝、深拷贝和属性复制</li>
 *   <li>对象映射：对象与Map、JSON的相互转换</li>
 *   <li>构建器模式：链式构建对象</li>
 *   <li>代理模式：方法拦截和AOP</li>
 *   <li>依赖注入：模拟简单的依赖注入容器</li>
 *   <li>性能测试：测试各种操作的性能表现</li>
 * </ul>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ReflectionDemo {

    private static final ReflectionToolkit toolkit = ReflectionToolkit.getInstance();

    public static void main(String[] args) {
        System.out.println("========== Java 8 反射工具包演示 ==========\n");

        // 1. 包扫描场景
        demonstratePackageScanning();

        // 2. 实例创建场景
        demonstrateInstanceCreation();

        // 3. 字段操作场景
        demonstrateFieldOperations();

        // 4. 方法调用场景
        demonstrateMethodInvocation();

        // 5. 对象复制场景
        demonstrateObjectCopy();

        // 6. 对象映射场景
        demonstrateObjectMapping();

        // 7. 构建器模式场景
        demonstrateBuilderPattern();

        // 8. 代理模式场景
        demonstrateProxyPattern();

        // 9. 依赖注入场景
        demonstrateDependencyInjection();

        // 10. 性能测试
        demonstratePerformance();

        // 11. 缓存统计
        demonstrateCacheStatistics();
    }

    private static void demonstratePackageScanning() {
        System.out.println("【1. 包扫描场景】");

        // 扫描所有实体类
        Set<Class<?>> entities = toolkit.scanPackage("io.github.qwzhang01.reflection.demo.model");
        System.out.println("扫描到的实体类: " + entities.size() + " 个");
        entities.forEach(c -> System.out.println("  - " + c.getSimpleName()));

        // 查找带注解的类
        Set<Class<?>> annotatedClasses = toolkit.findClassesWithAnnotation(
                "io.github.qwzhang01.reflection.demo.model", Entity.class
        );
        System.out.println("\n带 @Entity 注解的类: " + annotatedClasses.size() + " 个");

        // 查找子类
        Set<Class<? extends BaseEntity>> subClasses = toolkit.findSubClasses(
                "io.github.qwzhang01.reflection.demo.model", BaseEntity.class
        );
        System.out.println("BaseEntity 的子类: " + subClasses.size() + " 个");
        System.out.println();
    }

    private static void demonstrateInstanceCreation() {
        System.out.println("【2. 实例创建场景】");

        // 无参构造器
        User user1 = toolkit.newInstance(User.class);
        System.out.println("创建实例（无参）: " + user1);

        // 有参构造器
        User user2 = toolkit.newInstance(User.class, "张三", 25, "zhangsan@example.com");
        System.out.println("创建实例（有参）: " + user2);

        // 批量创建
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            users.add(toolkit.newInstance(User.class));
        }
        System.out.println("批量创建: " + users.size() + " 个实例");
        System.out.println();
    }

    private static void demonstrateFieldOperations() {
        System.out.println("【3. 字段操作场景】");

        User user = new User("李四", 30, "lisi@example.com");

        // 获取所有字段
        List<Field> fields = toolkit.getAllFields(User.class);
        System.out.println("所有字段: " + fields.stream()
                .map(Field::getName)
                .reduce((a, b) -> a + ", " + b).orElse(""));

        // 读取字段值
        String name = (String) toolkit.getFieldValue(user, "name");
        System.out.println("name 字段值: " + name);

        // 修改字段值（包括私有字段）
        toolkit.setFieldValue(user, "name", "王五");
        toolkit.setFieldValue(user, "age", 35);
        System.out.println("修改后: " + user);

        // 获取带注解的字段
        List<Field> annotatedFields = toolkit.getFieldsWithAnnotation(
                User.class, Column.class
        );
        System.out.println("带 @Column 注解的字段: " + annotatedFields.size() + " 个");

        // 获取指定类型的字段
        List<Field> stringFields = toolkit.getFieldsByType(User.class, String.class);
        System.out.println("String 类型字段: " + stringFields.size() + " 个");
        System.out.println();
    }

    private static void demonstrateMethodInvocation() {
        System.out.println("【4. 方法调用场景】");

        User user = new User("赵六", 28, "zhaoliu@example.com");

        // 调用getter方法
        String name = (String) toolkit.invokeMethod(user, "getName");
        System.out.println("调用 getName(): " + name);

        // 调用setter方法
        toolkit.invokeMethod(user, "setAge", 29);
        System.out.println("调用 setAge(29): " + user);

        // 获取所有getter方法
        List<Method> getters = toolkit.getGetters(User.class);
        System.out.println("Getter 方法: " + getters.size() + " 个");

        // 获取所有setter方法
        List<Method> setters = toolkit.getSetters(User.class);
        System.out.println("Setter 方法: " + setters.size() + " 个");

        // 批量调用getter
        System.out.println("\n批量调用 getter:");
        for (Method getter : getters) {
            Object value = toolkit.invokeMethod(user, getter.getName());
            System.out.println("  " + getter.getName() + "() = " + value);
        }
        System.out.println();
    }

    private static void demonstrateObjectCopy() {
        System.out.println("【5. 对象复制场景】");

        User source = new User("原对象", 40, "source@example.com");
        source.setId(1L);

        // 浅拷贝
        User shallowCopy = toolkit.shallowCopy(source);
        System.out.println("浅拷贝: " + shallowCopy);

        // 深拷贝
        User deepCopy = toolkit.deepCopy(source);
        System.out.println("深拷贝: " + deepCopy);

        // 属性复制
        User target = new User();
        toolkit.copyProperties(source, target);
        System.out.println("属性复制: " + target);

        // 忽略指定字段复制
        User target2 = new User();
        toolkit.copyProperties(source, target2, "id", "email");
        System.out.println("忽略 id 和 email: " + target2);
        System.out.println();
    }

    private static void demonstrateObjectMapping() {
        System.out.println("【6. 对象映射场景】");

        User user = new User("映射测试", 45, "mapper@example.com");
        user.setId(100L);

        // 对象转Map
        Map<String, Object> map = toolkit.toMap(user);
        System.out.println("对象转Map: " + map);

        // Map转对象
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("name", "从Map创建");
        sourceMap.put("age", 50);
        sourceMap.put("email", "frommap@example.com");
        User fromMap = toolkit.fromMap(sourceMap, User.class);
        System.out.println("Map转对象: " + fromMap);

        // 对象转JSON
        String json = toolkit.toJson(user);
        System.out.println("对象转JSON: " + json);

        // JSON转对象
        User fromJson = toolkit.fromJson(json, User.class);
        System.out.println("JSON转对象: " + fromJson);
        System.out.println();
    }

    private static void demonstrateBuilderPattern() {
        System.out.println("【7. 构建器模式场景】");

        // 链式构建对象
        User user = toolkit.builder(User.class)
                .set("name", "构建者")
                .set("age", 55)
                .set("email", "builder@example.com")
                .invoke("setId", 200L)
                .build();

        System.out.println("链式构建: " + user);

        // 批量设置
        Map<String, Object> config = ObjectBuilder.config()
                .add("name", "批量设置")
                .add("age", 60)
                .add("email", "batch@example.com")
                .build();

        User user2 = toolkit.builder(User.class)
                .setAll(config)
                .build();

        System.out.println("批量设置: " + user2);

        // 链式获取
        String name = (String) toolkit.builder(user).get("name");
        System.out.println("链式获取 name: " + name);
        System.out.println();
    }

    private static void demonstrateProxyPattern() {
        System.out.println("【8. 代理模式场景】");

        // 创建原始服务
        UserService originalService = new UserServiceImpl();

        // 创建带日志的代理
        UserService loggedService = toolkit.createProxy(
                originalService,
                new ReflectionProxy.LoggingInterceptor()
        );

        System.out.println("调用代理方法:");
        User user = loggedService.findById(1L);
        System.out.println("返回结果: " + user);

        // 创建带多个拦截器的代理
        UserService enhancedService = toolkit.createProxy(
                originalService,
                new ReflectionProxy.LoggingInterceptor(),
                new ReflectionProxy.PerformanceInterceptor(1000000), // 1ms
                new ReflectionProxy.CacheInterceptor()
        );

        System.out.println("\n多拦截器代理:");
        enhancedService.findById(2L);
        enhancedService.findById(2L); // 第二次调用会从缓存返回
        System.out.println();
    }

    private static void demonstrateDependencyInjection() {
        System.out.println("【9. 依赖注入场景】");

        // 扫描所有服务类
        Set<Class<?>> services = toolkit.scanPackage(
                "io.github.qwzhang01.reflection.demo.service",
                clazz -> !clazz.isInterface()
        );

        System.out.println("扫描到的服务类: " + services.size() + " 个");

        // 创建服务实例并注入依赖
        Map<Class<?>, Object> serviceInstances = new HashMap<>();

        for (Class<?> serviceClass : services) {
            Object instance = toolkit.newInstance(serviceClass);
            serviceInstances.put(serviceClass, instance);
            System.out.println("创建服务实例: " + serviceClass.getSimpleName());
        }

        // 模拟依赖注入
        for (Object service : serviceInstances.values()) {
            List<Field> fields = toolkit.getAllFields(service.getClass());
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> fieldType = field.getType();
                    Object dependency = serviceInstances.get(fieldType);
                    if (dependency != null) {
                        toolkit.setFieldValue(service, field.getName(), dependency);
                        System.out.println("注入依赖: " + field.getName() +
                                " -> " + fieldType.getSimpleName());
                    }
                }
            }
        }
        System.out.println();
    }

    private static void demonstratePerformance() {
        System.out.println("【10. 性能测试】");

        int iterations = 100000;
        User user = new User("性能测试", 65, "perf@example.com");

        // 测试字段访问
        long start1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            toolkit.getFieldValue(user, "name");
        }
        long end1 = System.nanoTime();
        System.out.printf("字段访问 %,d 次: %.2f ms (平均 %.0f ns/次)\n",
                iterations, (end1 - start1) / 1_000_000.0,
                (end1 - start1) / (double) iterations);

        // 测试方法调用
        long start2 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            toolkit.invokeMethod(user, "getName");
        }
        long end2 = System.nanoTime();
        System.out.printf("方法调用 %,d 次: %.2f ms (平均 %.0f ns/次)\n",
                iterations, (end2 - start2) / 1_000_000.0,
                (end2 - start2) / (double) iterations);

        // 测试实例创建
        long start3 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            toolkit.newInstance(User.class);
        }
        long end3 = System.nanoTime();
        System.out.printf("实例创建 %,d 次: %.2f ms (平均 %.0f ns/次)\n",
                iterations, (end3 - start3) / 1_000_000.0,
                (end3 - start3) / (double) iterations);

        // 测试对象复制
        long start4 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            toolkit.shallowCopy(user);
        }
        long end4 = System.nanoTime();
        System.out.printf("对象复制 %,d 次: %.2f ms (平均 %.0f ns/次)\n",
                iterations, (end4 - start4) / 1_000_000.0,
                (end4 - start4) / (double) iterations);
        System.out.println();
    }

    private static void demonstrateCacheStatistics() {
        System.out.println("【11. 缓存统计】");
        System.out.println(toolkit.getCacheStatistics());
        System.out.println();
    }
}
