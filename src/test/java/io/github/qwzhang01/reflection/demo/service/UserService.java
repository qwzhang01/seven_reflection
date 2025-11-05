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
 * 用户服务接口
 * <p>
 * 定义用户相关的业务操作，用于演示动态代理和方法拦截。
 * </p>
 *
 * @author avinzhang
 * @since 1.0
 */
public interface UserService {

    User findById(Long id);

    void save(User user);

    void update(User user);

    void delete(Long id);
}
