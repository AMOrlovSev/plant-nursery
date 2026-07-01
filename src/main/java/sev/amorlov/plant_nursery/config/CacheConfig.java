package sev.amorlov.plant_nursery.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 1. Базовая конфигурация по умолчанию (дефолтный TTL = 10 минут)
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                // Отключаем кэширование null-значений, чтобы не забивать память
                .disableCachingNullValues()
                // Настраиваем сериализацию ключей в обычную строку
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Настраиваем сериализацию значений в JSON через Jackson
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 2. Индивидуальные настройки времени жизни (TTL) для разных кэшей
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Списки растений с фильтрами — живут 5 минут
        cacheConfigurations.put("plants", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));

        // Конкретная карточка растения по ID — живет 1 час
        cacheConfigurations.put("plant", defaultCacheConfig.entryTtl(Duration.ofHours(1)));

        // 3. Собираем CacheManager со всеми правилами
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig) // если появится новый кэш, применится этот дефолт
                .withInitialCacheConfigurations(cacheConfigurations) // наши кастомные TTL
                .build();
    }
}