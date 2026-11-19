package com.example.gateway.config;


import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson.SecurityJacksonModules;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
public class SessionConfiguration implements BeanClassLoaderAware {

    private ClassLoader loader;

    /**
     * Note that the bean name for this bean is intentionally
     * {@code springSessionDefaultRedisSerializer}. It must be named this way to override
     * the default {@link RedisSerializer} used by Spring Session.
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new JacksonJsonRedisSerializer<>(objectMapper(), Object.class);
    }

    /**
     * Customized {@link JsonMapper} to add mix-in for class that doesn't have default
     * constructors
     * @return the {@link JsonMapper} to use
     */
    private JsonMapper objectMapper() {
        BasicPolymorphicTypeValidator.Builder validatorBuilder = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(java.net.URL.class);  // ← key line

        return JsonMapper.builder()
                .addModules(SecurityJacksonModules.getModules(this.loader, validatorBuilder))
                .build();
    }

    /*
     * @see
     * org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang
     * .ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }

}