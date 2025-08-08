package com.k8s.gpu.batch;

import com.k8s.gpu.entity.GpuDevice;
import com.k8s.gpu.entity.GpuUsageMetrics;
import com.k8s.gpu.repository.GpuDeviceRepository;
import com.k8s.gpu.repository.GpuUsageMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GPU 메트릭 배치 작업 테스트")
class GpuMetricsBatchJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job gpuMetricsCollectionJob;

    @Mock
    private GpuDeviceRepository gpuDeviceRepository;

    @Mock
    private GpuUsageMetricsRepository gpuUsageMetricsRepository;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(gpuMetricsCollectionJob);
    }

    @Test
    @DisplayName("GPU 메트릭 수집 배치 작업 성공")
    void gpuMetricsCollectionJob_Success() throws Exception {
        // Given
        GpuDevice testDevice = new GpuDevice();
        testDevice.setDeviceId("test-gpu-001");
        testDevice.setDeviceStatus(GpuDevice.DeviceStatus.ACTIVE);

        when(gpuDeviceRepository.findByDeviceStatusIn(any()))
                .thenReturn(Arrays.asList(testDevice));

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // When
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // Then
        assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
        assertTrue(jobExecution.getStepExecutions().size() > 0);
    }
}