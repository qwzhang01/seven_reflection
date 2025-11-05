# Seven Reflection - Java Reflection Toolkit

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.qwzhang01/seven_reflection.svg)](https://search.maven.org/artifact/io.github.qwzhang01/seven_reflection)

A powerful and easy-to-use Java reflection toolkit based on multiple design patterns, providing a concise API to simplify reflection operations.

## ✨ Features

- 🚀 **Easy to Use** - Provides a unified facade API without directly manipulating complex reflection APIs
- 🎯 **Comprehensive Features** - Covers common reflection scenarios including package scanning, instance creation, field/method access, object copying, Bean mapping, and more
- 🔧 **Elegant Design** - Based on multiple design patterns such as Facade, Factory, Builder, Proxy, etc.
- ⚡ **Performance Optimized** - Built-in caching mechanism to reduce repeated reflection call overhead
- 🛡️ **Type Safe** - Supports generics and provides type-safe APIs
- 📦 **Zero Dependencies** - Core functionality doesn't depend on any third-party libraries (except for testing)

## 📋 Table of Contents

- [Quick Start](#quick-start)
- [Core Features](#core-features)
  - [Package Scanning](#1-package-scanning)
  - [Instance Creation](#2-instance-creation)
  - [Field Operations](#3-field-operations)
  - [Method Invocation](#4-method-invocation)
  - [Object Copying](#5-object-copying)
  - [Object Mapping](#6-object-mapping)
  - [Builder Pattern](#7-builder-pattern)
  - [Dynamic Proxy](#8-dynamic-proxy)
- [Design Patterns](#design-patterns)
- [Performance Optimization](#performance-optimization)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## 🚀 Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.qwzhang01</groupId>
    <artifactId>seven_reflection</artifactId>
    <version>1.0</version>
</dependency>
```

### Basic Usage

```java
// Get toolkit instance (singleton)
ReflectionToolkit toolkit = ReflectionToolkit.getInstance();

// Create object
User user = toolkit.newInstance(User.class);

// Set field values (supports private fields)
toolkit.setFieldValue(user, "name", "John");
toolkit.setFieldValue(user, "age", 25);

// Invoke methods
toolkit.invokeMethod(user, "setEmail", "john@example.com");

// Get field value
String name = (String) toolkit.getFieldValue(user, "name");
System.out.println("Name: " + name);
```

## 🎯 Core Features

### 1. Package Scanning

Scan classes under specified packages with support for conditional filtering and inheritance relationship lookup.

```java
// Scan all classes
Set<Class<?>> classes = toolkit.scanPackage("com.example.model");

// Find classes with annotation
Set<Class<?>> entities = toolkit.findClassesWithAnnotation(
    "com.example.model", 
    Entity.class
);

// Find subclasses
Set<Class<? extends BaseEntity>> subClasses = toolkit.findSubClasses(
    "com.example.model", 
    BaseEntity.class
);

// Custom filter
Set<Class<?>> filtered = toolkit.scanPackage(
    "com.example",
    clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())
);
```

### 2. Instance Creation

Create object instances via reflection, supporting both no-arg and parameterized constructors.

```java
// No-arg constructor
User user1 = toolkit.newInstance(User.class);

// Parameterized constructor (auto-match parameter types)
User user2 = toolkit.newInstance(User.class, "John", 25, "john@example.com");

// Specify parameter types
Class<?>[] paramTypes = {String.class, Integer.class, String.class};
User user3 = toolkit.newInstance(User.class, paramTypes, "Jane", 30, "jane@example.com");
```

### 3. Field Operations

Access and modify object fields, including private fields.

```java
// Get all fields (including inherited fields)
List<Field> fields = toolkit.getAllFields(User.class);

// Get field value
String name = (String) toolkit.getFieldValue(user, "name");

// Set field value (auto-handle private fields)
toolkit.setFieldValue(user, "age", 26);

// Filter fields by annotation
List<Field> columnFields = toolkit.getFieldsWithAnnotation(User.class, Column.class);

// Filter fields by type
List<Field> stringFields = toolkit.getFieldsByType(User.class, String.class);
```

### 4. Method Invocation

Dynamically invoke object methods and static methods.

```java
// Invoke instance method
String name = (String) toolkit.invokeMethod(user, "getName");
toolkit.invokeMethod(user, "setAge", 27);

// Invoke static method
Class<?>[] paramTypes = {String.class};
toolkit.invokeStaticMethod(Utils.class, "formatDate", paramTypes, "2025-01-01");

// Get getter methods
List<Method> getters = toolkit.getGetters(User.class);

// Get setter methods
List<Method> setters = toolkit.getSetters(User.class);

// Batch invoke getters
for (Method getter : getters) {
    Object value = toolkit.invokeMethod(user, getter.getName());
    System.out.println(getter.getName() + "() = " + value);
}
```

### 5. Object Copying

Supports shallow copy, deep copy, and property copying.

```java
// Shallow copy
User copy1 = toolkit.shallowCopy(user);

// Deep copy
User copy2 = toolkit.deepCopy(user);

// Property copy (same name and type fields)
User target = new User();
toolkit.copyProperties(user, target);

// Ignore specified fields
toolkit.copyProperties(user, target, "id", "createTime");
```

### 6. Object Mapping

Conversion between objects and Map/JSON.

```java
// Object to Map
Map<String, Object> map = toolkit.toMap(user);

// Map to Object
Map<String, Object> data = new HashMap<>();
data.put("name", "Tom");
data.put("age", 28);
User newUser = toolkit.fromMap(data, User.class);

// Object to JSON (simple implementation)
String json = toolkit.toJson(user);

// JSON to Object (simple implementation)
User userFromJson = toolkit.fromJson(json, User.class);
```

> **Note**: Built-in JSON conversion is a simple implementation, only supports basic types. For production environments, it's recommended to use professional libraries like Jackson or Gson.

### 7. Builder Pattern

Provides fluent API for building objects.

```java
// Build from class
User user = toolkit.builder(User.class)
    .set("name", "Bob")
    .set("age", 30)
    .set("email", "bob@example.com")
    .invoke("setId", 1L)
    .build();

// Build from instance
User updated = toolkit.builder(user)
    .set("age", 31)
    .invoke("updateTime")
    .build();

// Batch setting
Map<String, Object> config = ObjectBuilder.config()
    .add("name", "Alice")
    .add("age", 32)
    .build();

User user2 = toolkit.builder(User.class)
    .setAll(config)
    .build();
```

### 8. Dynamic Proxy

Create proxy objects with support for method interception and AOP.

```java
// Create proxy with logging
UserService service = new UserServiceImpl();
UserService proxy = toolkit.createProxy(
    service,
    new ReflectionProxy.LoggingInterceptor()
);

// Method calls will be intercepted and logged
User user = proxy.findById(1L);

// Combine multiple interceptors
UserService enhanced = toolkit.createProxy(
    service,
    new ReflectionProxy.LoggingInterceptor(),
    new ReflectionProxy.PerformanceInterceptor(1000000), // 1ms threshold
    new ReflectionProxy.CacheInterceptor()
);
```

#### Custom Interceptor

```java
public class CustomInterceptor implements ReflectionProxy.MethodInterceptor {
    @Override
    public Object intercept(Object target, Method method, Object[] args, 
                          InterceptorChain chain) throws Throwable {
        // Pre-processing
        System.out.println("Before: " + method.getName());
        
        // Call next interceptor or target method
        Object result = chain.proceed(target, method, args);
        
        // Post-processing
        System.out.println("After: " + method.getName());
        
        return result;
    }
}
```

## 🎨 Design Patterns

This project adopts multiple design patterns to provide an elegant and extensible architecture:

| Design Pattern | Application Scenario | Implementation Class |
|---------------|----------------------|---------------------|
| **Singleton** | Global reflection context | `ReflectionContext` |
| **Facade** | Unified API entry | `ReflectionToolkit`, `FieldAccessor`, `MethodAccessor` |
| **Factory** | Object instance creation | `InstanceFactory` |
| **Builder** | Fluent object building | `ObjectBuilder` |
| **Prototype** | Object copying | `ObjectCopier` |
| **Adapter** | Bean mapping conversion | `BeanMapper` |
| **Proxy** | Method interception AOP | `ReflectionProxy` |
| **Strategy** | Class scanning strategy | `ClassScanner` |
| **Composite** | Multiple scanner composition | `CompositeScanner` |
| **Flyweight** | Cache class metadata | `ClassMetadata` |
| **Chain of Responsibility** | Interceptor chain | `InterceptorChain` |

## ⚡ Performance Optimization

### Caching Mechanism

The toolkit has built-in multi-level caching to significantly improve reflection operation performance:

```java
// View cache statistics
ReflectionContext.CacheStatistics stats = toolkit.getCacheStatistics();
System.out.println(stats);
// Output: CacheStatistics{classes=10, metadata=15}

// Clear cache
toolkit.clearCache();
```

### Performance Test Results

In standard test scenarios (100,000 operations):

| Operation Type | Average Time | Description |
|---------------|--------------|-------------|
| Field Access | ~50 ns/op | Including cache lookup |
| Method Invocation | ~80 ns/op | Including parameter matching |
| Instance Creation | ~200 ns/op | Using no-arg constructor |
| Object Copy | ~150 ns/op | Shallow copy |

> Performance data based on test environment with Intel i7 processor, 16GB RAM

## 📚 API Documentation

### Main Interface

#### ReflectionToolkit

Main entry point for the reflection toolkit, providing access to all core features.

```java
// Get singleton instance
ReflectionToolkit toolkit = ReflectionToolkit.getInstance();

// Package Scanning
Set<Class<?>> scanPackage(String packageName)
Set<Class<?>> scanPackage(String packageName, Predicate<Class<?>> filter)
Set<Class<?>> findClassesWithAnnotation(String packageName, Class<? extends Annotation> annotation)
Set<Class<? extends T>> findSubClasses(String packageName, Class<T> superClass)

// Instance Creation
<T> T newInstance(Class<T> clazz)
<T> T newInstance(Class<T> clazz, Object... args)
<T> T newInstance(Class<T> clazz, Class<?>[] paramTypes, Object... args)

// Field Operations
List<Field> getAllFields(Class<?> clazz)
Object getFieldValue(Object obj, String fieldName)
void setFieldValue(Object obj, String fieldName, Object value)
List<Field> getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation)
List<Field> getFieldsByType(Class<?> clazz, Class<?> fieldType)

// Method Invocation
List<Method> getAllMethods(Class<?> clazz)
Object invokeMethod(Object obj, String methodName, Object... args)
Object invokeStaticMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes, Object... args)
List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation)
List<Method> getGetters(Class<?> clazz)
List<Method> getSetters(Class<?> clazz)

// Object Copying
<T> T shallowCopy(T source)
<T> T deepCopy(T source)
void copyProperties(Object source, Object target, String... ignoreFields)

// Object Mapping
Map<String, Object> toMap(Object obj)
<T> T fromMap(Map<String, Object> map, Class<T> clazz)
String toJson(Object obj)
<T> T fromJson(String json, Class<T> clazz)

// Builder
<T> ObjectBuilder<T> builder(Class<T> clazz)
<T> ObjectBuilder<T> builder(T instance)

// Proxy
<T> T createProxy(T target, MethodInterceptor... interceptors)

// Cache Management
void clearCache()
CacheStatistics getCacheStatistics()
```

## 🔧 Advanced Usage

### Dependency Injection Example

```java
// Scan service classes
Set<Class<?>> services = toolkit.scanPackage(
    "com.example.service",
    clazz -> !clazz.isInterface()
);

// Create service instance container
Map<Class<?>, Object> container = new HashMap<>();
for (Class<?> serviceClass : services) {
    Object instance = toolkit.newInstance(serviceClass);
    container.put(serviceClass, instance);
}

// Inject dependencies
for (Object service : container.values()) {
    List<Field> fields = toolkit.getAllFields(service.getClass());
    for (Field field : fields) {
        if (field.isAnnotationPresent(Autowired.class)) {
            Class<?> fieldType = field.getType();
            Object dependency = container.get(fieldType);
            if (dependency != null) {
                toolkit.setFieldValue(service, field.getName(), dependency);
            }
        }
    }
}
```

### ORM Mapping Example

```java
// Define entity class
@Entity("user")
public class User {
    @Column("user_id")
    private Long id;
    
    @Column("user_name")
    private String name;
    
    // getters and setters...
}

// Get table name
Entity entity = User.class.getAnnotation(Entity.class);
String tableName = entity.value(); // "user"

// Get column mapping
List<Field> fields = toolkit.getFieldsWithAnnotation(User.class, Column.class);
Map<String, String> columnMap = new HashMap<>();
for (Field field : fields) {
    Column column = field.getAnnotation(Column.class);
    columnMap.put(field.getName(), column.value());
}
// {id=user_id, name=user_name}
```

## 📦 Project Structure

```
seven_reflection/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── io/github/qwzhang01/reflection/
│   │           ├── ReflectionToolkit.java          # Main entry (facade)
│   │           ├── accessor/                        # Accessors
│   │           │   ├── FieldAccessor.java          # Field access
│   │           │   └── MethodAccessor.java         # Method access
│   │           ├── builder/                         # Builder
│   │           │   └── ObjectBuilder.java          # Object builder
│   │           ├── copier/                          # Copier
│   │           │   └── ObjectCopier.java           # Object copy
│   │           ├── core/                            # Core
│   │           │   ├── ClassMetadata.java          # Class metadata
│   │           │   ├── ReflectionConfig.java       # Configuration
│   │           │   └── ReflectionContext.java      # Context
│   │           ├── factory/                         # Factory
│   │           │   └── InstanceFactory.java        # Instance factory
│   │           ├── mapper/                          # Mapper
│   │           │   └── BeanMapper.java             # Bean mapping
│   │           ├── proxy/                           # Proxy
│   │           │   └── ReflectionProxy.java        # Dynamic proxy
│   │           └── scanner/                         # Scanner
│   │               ├── ClassScanner.java           # Scanner interface
│   │               ├── CompositeScanner.java       # Composite scanner
│   │               ├── FileSystemScanner.java      # File system scanner
│   │               └── JarScanner.java             # JAR scanner
│   └── test/
│       └── java/
│           └── io/github/qwzhang01/reflection/
│               ├── demo/                            # Demo programs
│               │   ├── ReflectionDemo.java         # Complete demo
│               │   ├── model/                       # Model classes
│               │   └── service/                     # Service classes
│               └── test/                            # Unit tests
│                   └── ReflectionToolKitTest.java
├── pom.xml
└── README.md
```

## 🧪 Running Examples

### Demo Program

```bash
# Compile project
mvn clean compile

# Run demo program
mvn exec:java -Dexec.mainClass="io.github.qwzhang01.reflection.demo.ReflectionDemo"
```

### Unit Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=ReflectionToolKitTest
```

## 🤝 Contributing

Contributions, issue reports, and suggestions are welcome!

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Submit a Pull Request

### Coding Standards

- Follow Java coding conventions
- All public APIs must have complete JavaDoc comments
- New features require accompanying unit tests
- Keep code concise and readable

## 📄 License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

```
Copyright 2025 avinzhang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 📧 Contact

- **Author**: avinzhang
- **Email**: avinzhang@tencent.com
- **GitHub**: https://github.com/qwzhang01/seven_reflection

## 🙏 Acknowledgments

Thanks to all developers who have contributed to this project!

---

If this project helps you, please give it a ⭐️ Star!
