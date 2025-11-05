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
package io.github.qwzhang01.reflection.test;

import io.github.qwzhang01.reflection.ReflectionToolkit;
import io.github.qwzhang01.reflection.core.ReflectionContext;
import io.github.qwzhang01.reflection.demo.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 反射工具包单元测试
 * <p>
 * 测试反射工具包的核心功能，包括实例创建、字段操作、方法调用、
 * 对象复制、对象映射、构建器等功能。
 * </p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class ReflectionToolKitTest {


    private ReflectionToolkit toolkit;

    @Before
    public void setUp() {
        toolkit = ReflectionToolkit.getInstance();
    }

    @Test
    public void testCacheClear() {
        Set<Class<?>> classes = toolkit.scanPackage("");

    }

    @Test
    public void testInstanceCreation() {
        User user = toolkit.newInstance(User.class);
        assertNotNull(user);

        User user2 = toolkit.newInstance(User.class, "测试", 25, "test@example.com");
        assertEquals("测试", user2.getName());
        assertEquals(Integer.valueOf(25), user2.getAge());
    }

    @Test
    public void testFieldOperations() {
        User user = new User("张三", 30, "zhangsan@example.com");

        // 获取字段值
        String name = (String) toolkit.getFieldValue(user, "name");
        assertEquals("张三", name);

        // 设置字段值
        toolkit.setFieldValue(user, "name", "李四");
        assertEquals("李四", user.getName());

        // 获取所有字段
        List<Field> fields = toolkit.getAllFields(User.class);
        assertTrue(fields.size() > 0);
    }

    @Test
    public void testMethodInvocation() {
        User user = new User("王五", 35, "wangwu@example.com");

        // 调用方法
        String name = (String) toolkit.invokeMethod(user, "getName");
        assertEquals("王五", name);

        // 调用setter
        toolkit.invokeMethod(user, "setAge", 36);
        assertEquals(Integer.valueOf(36), user.getAge());
    }

    @Test
    public void testObjectCopy() {
        User source = new User("原对象", 40, "source@example.com");
        source.setId(1L);

        // 浅拷贝
        User copy = toolkit.shallowCopy(source);
        assertNotNull(copy);
        assertEquals(source.getName(), copy.getName());
        assertEquals(source.getAge(), copy.getAge());

        // 属性复制
        User target = new User();
        toolkit.copyProperties(source, target);
        assertEquals(source.getName(), target.getName());
    }

    @Test
    public void testObjectMapping() {
        User user = new User("映射", 45, "mapper@example.com");

        // 对象转Map
        Map<String, Object> map = toolkit.toMap(user);
        assertNotNull(map);
        assertEquals("映射", map.get("name"));

        // Map转对象
        User fromMap = toolkit.fromMap(map, User.class);
        assertEquals(user.getName(), fromMap.getName());
    }

    @Test
    public void testBuilder() {
        User user = toolkit.builder(User.class)
                .set("name", "构建者")
                .set("age", 50)
                .set("email", "builder@example.com")
                .build();

        assertEquals("构建者", user.getName());
        assertEquals(Integer.valueOf(50), user.getAge());
    }

    @Test
    public void testCacheStatistics() {
        ReflectionContext.CacheStatistics stats = toolkit.getCacheStatistics();
        assertNotNull(stats);
        assertTrue(stats.getClassCacheSize() >= 0);
        assertTrue(stats.getMetadataCacheSize() >= 0);
    }
}