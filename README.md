# spring-data-redis-bloom-filter
扩展RedisTemplate，增加对布隆过滤器的支持



一、示例程序

https://github.com/redooper/demo-parent/blob/master/spring-data-redis-bloom-filter-demo/src/main/java/com/redooper/bloom/filter/demo/BloomFilterApplication.java



二、使用步骤

1、参照网址，本地启动Redis实例

https://github.com/RedisBloom/RedisBloom

或

直接执行以下脚本

```shell
docker pull redislabs/rebloom:2.0.3
```

```shell
docker run -d \
-p 6380:6379 \
--name local-redisbf \
--restart=always \
redislabs/rebloom:2.0.3
```



2、构建`spring-data-redis-bloom-filter`项目

```shell
mvn clean install -Dmaven.test.skip=true
```



3、项目中引入`spring-data-redis-bloom-filter`依赖，并排除`lettuce-core`依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <exclusions>
        <exclusion>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis-bloom-filter</artifactId>
</dependency>
```



注意：必须使用`jedis`连接池，暂不支持`lettuce`连接池，在自动配置中做了相应的条件限制

```java
@Configuration
@ConditionalOnMissingClass("io.lettuce.core.RedisClient")
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
@ConditionalOnBean(StringRedisTemplate.class)
public class BloomAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BloomOperations.class)
    public BloomOperations bloomOperations(StringRedisTemplate redisTemplate) {
        return new DefaultBloomOperations(redisTemplate);
    }
}
```



4、直接注入`BloomOperations`对象，即可使用

```java
@Slf4j
@SpringBootApplication
public class BloomFilterApplication implements ApplicationRunner {

    @Autowired
    private BloomOperations bloomOperations;

    public static void main(String[] args) {
        SpringApplication.run(BloomFilterApplication.class, args);
    }

    /*
        日志输出如下：
        test add result: true
        test addMulti result: [false, true]
        test exists result: true
        test existsMulti result: [true, false]
        test delete result: true
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String key = "TEST";

        // 1.创建布隆过滤器
        bloomOperations.createFilter(key, 0.01, 100);

        // 2.添加一个元素
        Boolean foo = bloomOperations.add(key, "foo");
        log.info("test add result: {}", foo);

        // 3.批量添加元素
        Boolean[] addMulti = bloomOperations.addMulti(key, "foo", "bar");
        log.info("test addMulti result: {}", Arrays.toString(addMulti));

        // 4.校验一个元素是否存在
        Boolean exists = bloomOperations.exists(key, "foo");
        log.info("test exists result: {}", exists);

        // 5.批量校验元素是否存在
        Boolean[] existsMulti = bloomOperations.existsMulti(key, "foo", "foo1");
        log.info("test existsMulti result: {}", Arrays.toString(existsMulti));

        // 6.删除布隆过滤器
        Boolean delete = bloomOperations.delete(key);
        log.info("test delete result: {}", delete);
    }
}
```

