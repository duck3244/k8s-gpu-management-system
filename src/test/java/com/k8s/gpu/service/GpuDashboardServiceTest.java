package com.k8s.gpu.service;

import com.k8s.gpu.dto.GpuDashboardDto;
import com.k8s.gpu.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GPU 대시보드 서비스 테스트")
class GpuDashboardServiceTest {

    @Mock
    private GpuNodeRepository nodeRepository;

    @Mock
    private GpuDeviceRepository deviceRepository;

    @Mock
    private GpuUsageMetricsRepository metricsRepository;

    @Mock
    private SystemAlertsRepository alertsRepository;

    @Mock
    private GpuAllocationsRepository allocationsRepository;

    @InjectMocks
    private GpuDashboardService dashboardService;

    @BeforeEach
    void setUp() {
        // Mock 데이터 설정
    }

    @Test
    @DisplayName("전체 통계 조회 성공")
    void getOverallStats_Success() {
        // Given
        List<Object[]> nodeStats = Arrays.asList(
            new Object[]{"ACTIVE", 10},
            new Object[]{"MAINTENANCE", 2}
        );
        Object[] gpuStats = new Object[]{96, 73, 23, 15, 42};
        Object[] metricsStats = new Object[]{67.5, 18500.5};

        when(nodeRepository.getNodeStatusStatistics()).thenReturn(nodeStats);
        when(deviceRepository.getOverallGpuStatistics()).thenReturn(gpuStats);
        when(metricsRepository.getLatestMetricsStatistics()).thenReturn(metricsStats);

        // When
        GpuDashboardDto.OverallStats result = dashboardService.getOverallStats();

        // Then
        assertNotNull(result);
        assertEquals(12, result.getTotalNodes());
        assertEquals(10, result.getActiveNodes());
        assertEquals(96, result.getTotalGpus());
        assertEquals(73, result.getUsedGpus());
        assertEquals(23, result.getAvailableGpus());
        assertTrue(result.getOverallUtilization().compareTo(BigDecimal.valueOf(76.04)) == 0);
    }

    @Test
    @DisplayName("클러스터별 통계 조회 성공")
    void getClusterStats_Success() {
        // Given
        List<Object[]> clusterData = Arrays.asList(
            new Object[]{"gpu-cluster-prod", 8, 64, 16},
            new Object[]{"gpu-cluster-dev", 4, 32, 8}
        );

        when(nodeRepository.getClusterStatistics()).thenReturn(clusterData);

        // When
        List<GpuDashboardDto.ClusterStats> result = dashboardService.getClusterStats();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        GpuDashboardDto.ClusterStats prodStats = result.get(0);
        assertEquals("gpu-cluster-prod", prodStats.getClusterName());
        assertEquals(8, prodStats.getNodeCount());
        assertEquals(64, prodStats.getTotalGpus());
        assertEquals(48, prodStats.getUsedGpus()); // 64 - 16
        assertEquals(0, prodStats.getUtilizationRate().compareTo(BigDecimal.valueOf(75.0)));
    }

    @Test
    @DisplayName("활성 알림 조회 성공")
    void getActiveAlerts_Success() {
        // Given
        List<Object[]> alertData = Arrays.asList(
            new Object[]{"alert-001", "TEMPERATURE", "HIGH", "DEVICE", "gpu-001", "GPU 온도 초과", LocalDateTime.now(), "ACTIVE"},
            new Object[]{"alert-002", "UTILIZATION", "MEDIUM", "NODE", "node-001", "높은 사용률", LocalDateTime.now(), "ACTIVE"}
        );

        when(alertsRepository.getActiveAlerts()).thenReturn(alertData);

        // When
        List<GpuDashboardDto.AlertInfo> result = dashboardService.getActiveAlerts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        GpuDashboardDto.AlertInfo alert = result.get(0);
        assertEquals("alert-001", alert.getAlertId());
        assertEquals("TEMPERATURE", alert.getAlertType());
        assertEquals("HIGH", alert.getSeverity());
        assertEquals("DEVICE", alert.getTargetType());
    }

    @Test
    @DisplayName("대시보드 전체 데이터 조회 성공")
    void getDashboardData_Success() {
        // Given
        // 각 메서드에 대한 Mock 설정
        GpuDashboardDto.OverallStats overallStats = new GpuDashboardDto.OverallStats();
        overallStats.setTotalNodes(12);
        overallStats.setTotalGpus(96);

        when(nodeRepository.getNodeStatusStatistics()).thenReturn(Arrays.asList(new Object[]{"ACTIVE", 10}));
        when(deviceRepository.getOverallGpuStatistics()).thenReturn(new Object[]{96, 73, 23, 15, 42});
        when(metricsRepository.getLatestMetricsStatistics()).thenReturn(new Object[]{67.5, 18500.5});
        when(nodeRepository.getClusterStatistics()).thenReturn(Arrays.asList());
        when(deviceRepository.getModelStatistics()).thenReturn(Arrays.asList());
        when(metricsRepository.getUsageTrends(any())).thenReturn(Arrays.asList());
        when(alertsRepository.getActiveAlerts()).thenReturn(Arrays.asList());
        when(allocationsRepository.getRecentActivities(any())).thenReturn(Arrays.asList());

        // When
        GpuDashboardDto result = dashboardService.getDashboardData();

        // Then
        assertNotNull(result);
        assertNotNull(result.getOverallStats());
        assertNotNull(result.getClusterStats());
        assertNotNull(result.getModelStats());
        assertNotNull(result.getUsageTrends());
        assertNotNull(result.getActiveAlerts());
        assertNotNull(result.getRecentActivities());
    }
}