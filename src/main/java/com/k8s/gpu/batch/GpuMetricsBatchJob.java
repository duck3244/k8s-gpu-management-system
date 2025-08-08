package com.k8s.gpu.batch;

import com.k8s.gpu.entity.GpuDevice;
import com.k8s.gpu.entity.GpuUsageMetrics;
import com.k8s.gpu.service.GpuNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.k8s.gpu.repository.GpuDeviceRepository;
import com.k8s.gpu.repository.GpuUsageMetricsRepository;
import com.k8s.gpu.repository.GpuAllocationsRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * GPU 메트릭 수집 배치 작업 (Spring Boot 2.3.2 호환)
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GpuMetricsBatchJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final GpuDeviceRepository gpuDeviceRepository;
    private final GpuUsageMetricsRepository gpuUsageMetricsRepository;
    private final GpuMetricsCollectionService metricsCollectionService;

    @Value("${app.batch.metrics-collection.chunk-size:100}")
    private int chunkSize;

    @Value("${app.batch.metrics-collection.thread-pool-size:5}")
    private int threadPoolSize;

    /**
     * GPU 메트릭 수집 Job
     */
    @Bean
    public Job gpuMetricsCollectionJob() {
        return jobBuilderFactory.get("gpuMetricsCollectionJob")
                .incrementer(new RunIdIncrementer())
                .start(gpuMetricsCollectionStep())
                .build();
    }

    /**
     * GPU 메트릭 수집 Step
     */
    @Bean
    public Step gpuMetricsCollectionStep() {
        return stepBuilderFactory.get("gpuMetricsCollectionStep")
                .<GpuDevice, GpuUsageMetrics>chunk(chunkSize)
                .reader(gpuDeviceReader())
                .processor(gpuMetricsProcessor())
                .writer(gpuMetricsWriter())
                .build();
    }

    /**
     * GPU 장비 Reader
     */
    @Bean
    public RepositoryItemReader<GpuDevice> gpuDeviceReader() {
        return new RepositoryItemReaderBuilder<GpuDevice>()
                .name("gpuDeviceReader")
                .repository(gpuDeviceRepository)
                .methodName("findByDeviceStatusIn")
                .arguments(Collections.singletonList(
                    java.util.Arrays.asList(
                        GpuDevice.DeviceStatus.ACTIVE, 
                        GpuDevice.DeviceStatus.MIG_ENABLED
                    )
                ))
                .sorts(Collections.singletonMap("deviceId", Sort.Direction.ASC))
                .build();
    }

    /**
     * GPU 메트릭 Processor
     */
    @Bean
    public ItemProcessor<GpuDevice, GpuUsageMetrics> gpuMetricsProcessor() {
        return new ItemProcessor<GpuDevice, GpuUsageMetrics>() {
            @Override
            public GpuUsageMetrics process(GpuDevice device) throws Exception {
                try {
                    log.debug("GPU 메트릭 수집 처리 - deviceId: {}", device.getDeviceId());
                    
                    // 실제 환경에서는 nvidia-smi 또는 NVML을 통해 실제 메트릭을 수집
                    GpuUsageMetrics metrics = metricsCollectionService.collectMetrics(device);
                    
                    if (metrics != null) {
                        log.debug("GPU 메트릭 수집 완료 - deviceId: {}, utilization: {}%", 
                                device.getDeviceId(), metrics.getGpuUtilizationPct());
                        return metrics;
                    } else {
                        log.warn("GPU 메트릭 수집 실패 - deviceId: {}", device.getDeviceId());
                        return null;
                    }
                    
                } catch (Exception e) {
                    log.error("GPU 메트릭 처리 중 오류 - deviceId: {}", device.getDeviceId(), e);
                    return null;
                }
            }
        };
    }

    /**
     * GPU 메트릭 Writer
     */
    @Bean
    public RepositoryItemWriter<GpuUsageMetrics> gpuMetricsWriter() {
        return new RepositoryItemWriterBuilder<GpuUsageMetrics>()
                .repository(gpuUsageMetricsRepository)
                .methodName("save")
                .build();
    }
}

/**
 * GPU 메트릭 수집 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
class GpuMetricsCollectionService {

    /**
     * GPU 장비에서 메트릭 수집
     */
    public GpuUsageMetrics collectMetrics(GpuDevice device) {
        try {
            // 실제 구현에서는 nvidia-smi 또는 NVML API를 사용
            // 여기서는 시뮬레이션 데이터 생성
            GpuUsageMetrics metrics = new GpuUsageMetrics();
            
            // 메트릭 ID 생성
            String metricId = "METRIC_" + System.currentTimeMillis() + "_" + device.getDeviceId();
            metrics.setMetricId(metricId);
            metrics.setDeviceId(device.getDeviceId());
            
            // 시뮬레이션 데이터 생성 (실제로는 nvidia-smi에서 수집)
            metrics.setGpuUtilizationPct(generateRandomMetric(10.0, 95.0));
            metrics.setMemoryUsedMb(generateRandomInteger(1000, 8000));
            metrics.setMemoryTotalMb(8192);
            
            if (metrics.getMemoryUsedMb() != null && metrics.getMemoryTotalMb() != null) {
                double memoryUtilization = (metrics.getMemoryUsedMb().doubleValue() / 
                                          metrics.getMemoryTotalMb().doubleValue()) * 100;
                metrics.setMemoryUtilizationPct(BigDecimal.valueOf(memoryUtilization)
                    .setScale(2, java.math.RoundingMode.HALF_UP));
            }
            
            metrics.setTemperatureC(generateRandomMetric(40.0, 85.0));
            metrics.setPowerDrawW(generateRandomMetric(100.0, 350.0));
            metrics.setFanSpeedPct(generateRandomMetric(30.0, 80.0));
            metrics.setClockGraphicsMhz(generateRandomInteger(1200, 2100));
            metrics.setClockMemoryMhz(generateRandomInteger(1000, 1750));
            metrics.setPcieTxMbps(generateRandomMetric(100.0, 1000.0));
            metrics.setPcieRxMbps(generateRandomMetric(100.0, 1000.0));
            metrics.setProcessesCount(generateRandomInteger(0, 5));
            metrics.setTimestamp(LocalDateTime.now());
            metrics.setCollectionSource("nvidia-smi");
            
            return metrics;
            
        } catch (Exception e) {
            log.error("GPU 메트릭 수집 중 오류 발생 - deviceId: {}", device.getDeviceId(), e);
            return null;
        }
    }

    /**
     * 랜덤 메트릭 생성 (시뮬레이션용)
     */
    private BigDecimal generateRandomMetric(double min, double max) {
        double random = min + (Math.random() * (max - min));
        return BigDecimal.valueOf(random).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * 랜덤 정수 생성 (시뮬레이션용)
     */
    private Integer generateRandomInteger(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }
}

/**
 * GPU 메트릭 수집 스케줄러
 */
@Component
@RequiredArgsConstructor
@Slf4j
class GpuMetricsScheduler {

    private final JobLauncher jobLauncher;
    private final Job gpuMetricsCollectionJob;

    /**
     * 정기적인 GPU 메트릭 수집 (1분마다)
     */
    @Scheduled(fixedDelayString = "${app.gpu.metrics.collection.interval:60000}")
    public void collectGpuMetrics() {
        try {
            log.info("GPU 메트릭 수집 배치 작업 시작");
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            JobExecution jobExecution = jobLauncher.run(gpuMetricsCollectionJob, jobParameters);
            
            log.info("GPU 메트릭 수집 배치 작업 완료 - 상태: {}, 종료 코드: {}", 
                    jobExecution.getStatus(), jobExecution.getExitStatus().getExitCode());
                    
        } catch (Exception e) {
            log.error("GPU 메트릭 수집 배치 작업 실행 중 오류", e);
        }
    }
}

/**
 * GPU 최적화 스케줄러
 */
@Component
@RequiredArgsConstructor
@Slf4j
class GpuOptimizationScheduler {

    private final GpuAllocationsRepository allocationsRepository;
    private final GpuNodeService gpuNodeService;

    /**
     * GPU 자원 최적화 (30분마다)
     */
    @Scheduled(cron = "${app.gpu.optimization.schedule:0 */30 * * * *}")
    public void optimizeGpuResources() {
        try {
            log.info("GPU 자원 최적화 작업 시작");
            
            // 만료된 할당 정리
            cleanupExpiredAllocations();
            
            // 노드별 가용성 업데이트
            updateNodeAvailability();
            
            // MIG 인스턴스 최적화
            optimizeMigInstances();
            
            // 비용 계산 업데이트
            updateCostCalculations();
            
            log.info("GPU 자원 최적화 작업 완료");
            
        } catch (Exception e) {
            log.error("GPU 자원 최적화 작업 실행 중 오류", e);
        }
    }

    private void cleanupExpiredAllocations() {
        log.debug("만료된 할당 정리 시작");
        // TODO: 만료된 할당 정리 로직 구현
    }

    private void updateNodeAvailability() {
        log.debug("노드별 가용성 업데이트 시작");
        // TODO: 노드별 가용성 업데이트 로직 구현
    }

    private void optimizeMigInstances() {
        log.debug("MIG 인스턴스 최적화 시작");
        // TODO: MIG 인스턴스 최적화 로직 구현
    }

    private void updateCostCalculations() {
        log.debug("비용 계산 업데이트 시작");
        // TODO: 비용 계산 업데이트 로직 구현
    }
}