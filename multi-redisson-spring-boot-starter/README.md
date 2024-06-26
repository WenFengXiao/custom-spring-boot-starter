# multi-redisson-spring-boot-starter
![Static Badge](https://img.shields.io/badge/JDK-17-green?style=plastic) ![Static Badge](https://img.shields.io/badge/springBoot-3.2.3-green?style=plastic) ![Static Badge](https://img.shields.io/badge/redisson-3.31.0-green?style=plastic)

基于redisson创建的redis多数据源 springboot快速集成启动器，使用过程中如有问题和建议，请联系: [coldcicada69@gmail.com](coldcicada69@gmail.com)

## 特性

- 支持根据配置文件多数据源接入

## 约定

- 本启动器只提供快速连接redis并提供redissonClient Bean供开发人员对redis进行CRUD操作。

- redissonClient Bean名称采用name添加固定后缀(RedissonClient)的方式注册

- 在@RestController中无法使用@Autowired方式注入redissonClient，请先@Autowired SpringApplicationContext后使用下列方式获取。

  ```java
  applicationContext.getBean(beanName, RedissonClient.class)
  ```

## 使用方法

1. 引入sand-spring-boot-start-redisson

   ```
   <dependency>
        <groupId>com.coldcicada</groupId>
        <artifactId>multi-redisson-spring-boot-starter</artifactId>
        <version>1.0.0</version>
   </dependency>
   ```