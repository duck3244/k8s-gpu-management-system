// JpaConfig.java
package com.k8s.gpu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 설정
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.k8s.gpu.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
}