package com.shyam.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.data.redis.password:}")
  private String redisPassword;

  @Value("${spring.data.redis.ssl.enabled:false}")
  private boolean redisSsl;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {

    RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();

    redisConfig.setHostName(redisHost);
    redisConfig.setPort(redisPort);

    if (StringUtils.hasText(redisPassword)) {
      redisConfig.setUsername("default");
      redisConfig.setPassword(redisPassword);
    }

    LettuceClientConfiguration clientConfig =
        redisSsl
            ? LettuceClientConfiguration.builder().useSsl().build()
            : LettuceClientConfiguration.builder().build();

    return new LettuceConnectionFactory(redisConfig, clientConfig);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory connectionFactory) {

    RedisTemplate<String, String> template = new RedisTemplate<>();

    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new StringRedisSerializer());

    template.afterPropertiesSet();

    return template;
  }
}
