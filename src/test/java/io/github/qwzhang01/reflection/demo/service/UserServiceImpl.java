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
package io.github.qwzhang01.reflection.demo.service;


import io.github.qwzhang01.reflection.demo.model.User;

/**
 * 用户服务实现
 * <p>
 * UserService接口的实现类，提供用户管理的具体业务逻辑。
 * 用于演示动态代理和方法拦截功能。
 * </p>
 *
 * @author avinzhang
 * @since 1.0
 */
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Long id) {
        // 模拟数据库查询
        User user = new User("用户" + id, 25, "user" + id + "@example.com");
        user.setId(id);
        return user;
    }

    @Override
    public void save(User user) {
        System.out.println("保存用户: " + user);
    }

    @Override
    public void update(User user) {
        System.out.println("更新用户: " + user);
    }

    @Override
    public void delete(Long id) {
        System.out.println("删除用户: " + id);
    }
}
