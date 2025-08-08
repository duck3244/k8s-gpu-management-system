package com.k8s.gpu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * K8s GPU Management System Main Application
 * 
 * @author K8s GPU Team
 * @since 2025-08-01
 */
@SpringBootApplication
@EnableScheduling
@EnableBatchProcessing
@EnableTransactionManagement
public class K8sGpuManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sGpuManagementApplication.class, args);
    }
}