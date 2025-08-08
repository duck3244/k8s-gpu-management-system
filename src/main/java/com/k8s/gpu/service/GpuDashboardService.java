package com.k8s.gpu.service;

import com.k8s.gpu.dto.GpuDashboardDto;
import com.k8s.gpu.repository.GpuNodeRepository;
import com.k8s.gpu.repository.GpuDeviceRepository;
import com.k8s.gpu.repository.GpuUsageMetricsRepository;
import com.k8s.gpu.repository.SystemAlertsRepository;
import com.k8s.gpu.repository.GpuAllocationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GPU 대시보드 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GpuDashboardService {

    private final GpuNodeRepository nodeRepository;
    private final GpuDeviceRepository deviceRepository;
    private final GpuUsageMetricsRepository metricsRepository;
    private final SystemAlertsRepository alertsRepository;
    private final GpuAllocationsRepository allocationsRepository;

    /**
     * 대시보드 전체 데이터 조회
     */
    public GpuDashboardDto getDashboardData() {
        log.debug("GPU 대시보드 데이터 조회 시작");

        GpuDashboardDto dashboard = new GpuDashboardDto();
        
        // 전체 통계
        dashboard.setOverallStats(getOverallStats());
        
        // 클러스터별 통계
        dashboard.setClusterStats(getClusterStats());
        
        // GPU 모델별 통계
        dashboard.setModelStats(getModelStats());
        
        // 최근 사용량 추이 (지난 24시간)
        dashboard.setUsageTrends(getUsageTrends(24));
        
        // 활성 알림
        dashboard.setActiveAlerts(getActiveAlerts());
        
        // 최근 활동
        dashboard.setRecentActivities(getRecentActivities(50));

        log.debug("GPU 대시보드 데이터 조회 완료");
        return dashboard;
    }

    /**
     * 전체 통계 조회
     */
    public GpuDashboardDto.OverallStats getOverallStats() {
        log.debug("전체 GPU 통계 조회");

        GpuDashboardDto.OverallStats stats = new GpuDashboardDto.OverallStats();
        
        // 노드 통계
        List<Object[]> nodeStats = nodeRepository.getNodeStatusStatistics();
        int totalNodes = nodeStats.stream().mapToInt(row -> ((Number) row[1]).intValue()).sum();
        int activeNodes = nodeStats.stream()
            .filter(row -> "ACTIVE".equals(row[0].toString()))
            .mapToInt(row -> ((Number) row[1]).intValue())
            .findFirst().orElse(0);
            
        stats.setTotalNodes(totalNodes);
        stats.setActiveNodes(activeNodes);

        // GPU 통계
        Object[] gpuStats = deviceRepository.getOverallGpuStatistics();
        if (gpuStats != null && gpuStats.length >= 5) {
            stats.setTotalGpus(((Number) gpuStats[0]).intValue());
            stats.setUsedGpus(((Number) gpuStats[1]).intValue());
            stats.setAvailableGpus(((Number) gpuStats[2]).intValue());
            stats.setMigEnabledGpus(((Number) gpuStats[3]).intValue());
            stats.setTotalMigInstances(((Number) gpuStats[4]).intValue());
            
            // 사용률 계산
            int totalGpus = stats.getTotalGpus();
            int usedGpus = stats.getUsedGpus();
            if (totalGpus > 0) {
                BigDecimal utilization = BigDecimal.valueOf(usedGpus)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalGpus), 2, RoundingMode.HALF_UP);
                stats.setOverallUtilization(utilization);
            }
        }

        // 최근 메트릭 통계
        Object[] metricsStats = metricsRepository.getLatestMetricsStatistics();
        if (metricsStats != null && metricsStats.length >= 2) {
            stats.setAverageTemperature(((Number) metricsStats[0]).doubleValue() != 0 ? 
                BigDecimal.valueOf(((Number) metricsStats[0]).doubleValue()).setScale(1, RoundingMode.HALF_UP) : null);
            stats.setTotalPowerConsumption(((Number) metricsStats[1]).doubleValue() != 0 ? 
                BigDecimal.valueOf(((Number) metricsStats[1]).doubleValue()).setScale(1, RoundingMode.HALF_UP) : null);
        }

        return stats;
    }

    /**
     * 클러스터별 통계 조회
     */
    public List<GpuDashboardDto.ClusterStats> getClusterStats() {
        log.debug("클러스터별 GPU 통계 조회");

        List<Object[]> clusterData = nodeRepository.getClusterStatistics();
        
        return clusterData.stream().map(row -> {
            GpuDashboardDto.ClusterStats stats = new GpuDashboardDto.ClusterStats();
            stats.setClusterName((String) row[0]);
            stats.setNodeCount(((Number) row[1]).intValue());
            stats.setTotalGpus(((Number) row[2]).intValue());
            
            int totalGpus = stats.getTotalGpus();
            int availableGpus = ((Number) row[3]).intValue();
            int usedGpus = totalGpus - availableGpus;
            
            stats.setUsedGpus(usedGpus);
            
            if (totalGpus > 0) {
                BigDecimal utilization = BigDecimal.valueOf(usedGpus)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalGpus), 1, RoundingMode.HALF_UP);
                stats.setUtilizationRate(utilization);
            }
            
            // 클러스터 상태 결정 (간단한 로직)
            if (utilization != null && utilization.compareTo(BigDecimal.valueOf(90)) > 0) {
                stats.setStatus("HIGH_USAGE");
            } else if (availableGpus == 0) {
                stats.setStatus("FULL");
            } else {
                stats.setStatus("HEALTHY");
            }
            
            return stats;
        }).collect(Collectors.toList());
    }

    /**
     * GPU 모델별 통계 조회
     */
    public List<GpuDashboardDto.ModelStats> getModelStats() {
        log.debug("GPU 모델별 통계 조회");

        List<Object[]> modelData = deviceRepository.getModelStatistics();
        
        return modelData.stream().map(row -> {
            GpuDashboardDto.ModelStats stats = new GpuDashboardDto.ModelStats();
            stats.setModelName((String) row[0]);
            stats.setArchitecture((String) row[1]);
            stats.setDeviceCount(((Number) row[2]).intValue());
            stats.setUsedDevices(((Number) row[3]).intValue());
            
            int totalDevices = stats.getDeviceCount();
            int usedDevices = stats.getUsedDevices();
            
            if (totalDevices > 0) {
                BigDecimal utilization = BigDecimal.valueOf(usedDevices)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalDevices), 1, RoundingMode.HALF_UP);
                stats.setUtilizationRate(utilization);
            }
            
            // 평균 온도와 전력 소모 (최근 메트릭에서)
            Object[] modelMetrics = metricsRepository.getModelAverageMetrics((String) row[0]);
            if (modelMetrics != null && modelMetrics.length >= 2) {
                if (((Number) modelMetrics[0]).doubleValue() != 0) {
                    stats.setAverageTemperature(BigDecimal.valueOf(((Number) modelMetrics[0]).doubleValue())
                        .setScale(1, RoundingMode.HALF_UP));
                }
                if (((Number) modelMetrics[1]).doubleValue() != 0) {
                    stats.setAveragePowerConsumption(BigDecimal.valueOf(((Number) modelMetrics[1]).doubleValue())
                        .setScale(1, RoundingMode.HALF_UP));
                }
            }
            
            return stats;
        }).collect(Collectors.toList());
    }

    /**
     * 사용량 추이 조회
     */
    public List<GpuDashboardDto.UsageTrend> getUsageTrends(int hours) {
        log.debug("GPU 사용량 추이 조회 - 최근 {}시간", hours);

        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<Object[]> trendData = metricsRepository.getUsageTrends(since);
        
        return trendData.stream().map(row -> {
            GpuDashboardDto.UsageTrend trend = new GpuDashboardDto.UsageTrend();
            trend.setTimestamp((LocalDateTime) row[0]);
            
            if (((Number) row[1]).doubleValue() != 0) {
                trend.setOverallUtilization(BigDecimal.valueOf(((Number) row[1]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            if (((Number) row[2]).doubleValue() != 0) {
                trend.setMemoryUtilization(BigDecimal.valueOf(((Number) row[2]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            if (((Number) row[3]).doubleValue() != 0) {
                trend.setAverageTemperature(BigDecimal.valueOf(((Number) row[3]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            if (((Number) row[4]).doubleValue() != 0) {
                trend.setTotalPowerConsumption(BigDecimal.valueOf(((Number) row[4]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            
            return trend;
        }).collect(Collectors.toList());
    }

    /**
     * 활성 알림 조회
     */
    public List<GpuDashboardDto.AlertInfo> getActiveAlerts() {
        log.debug("활성 알림 조회");

        List<Object[]> alertData = alertsRepository.getActiveAlerts();
        
        return alertData.stream().map(row -> {
            GpuDashboardDto.AlertInfo alert = new GpuDashboardDto.AlertInfo();
            alert.setAlertId((String) row[0]);
            alert.setAlertType((String) row[1]);
            alert.setSeverity((String) row[2]);
            alert.setTargetType((String) row[3]);
            alert.setTargetId((String) row[4]);
            alert.setMessage((String) row[5]);
            alert.setCreatedDate((LocalDateTime) row[6]);
            alert.setStatus((String) row[7]);
            
            return alert;
        }).collect(Collectors.toList());
    }

    /**
     * 최근 활동 조회
     */
    public List<GpuDashboardDto.RecentActivity> getRecentActivities(int limit) {
        log.debug("최근 활동 조회 - 최대 {}건", limit);

        List<Object[]> activityData = allocationsRepository.getRecentActivities(limit);
        
        return activityData.stream().map(row -> {
            GpuDashboardDto.RecentActivity activity = new GpuDashboardDto.RecentActivity();
            activity.setActivityType(determineActivityType((String) row[0]));
            activity.setDescription(buildActivityDescription(row));
            activity.setTarget((String) row[1]);
            activity.setUser((String) row[2]);
            activity.setTimestamp((LocalDateTime) row[3]);
            activity.setStatus((String) row[4]);
            
            return activity;
        }).collect(Collectors.toList());
    }

    /**
     * 실시간 성능 메트릭 조회
     */
    public List<GpuDashboardDto.PerformanceMetrics> getRealtimeMetrics() {
        log.debug("실시간 GPU 성능 메트릭 조회");

        List<Object[]> metricsData = metricsRepository.getLatestDeviceMetrics();
        
        return metricsData.stream().map(row -> {
            GpuDashboardDto.PerformanceMetrics metrics = new GpuDashboardDto.PerformanceMetrics();
            metrics.setDeviceId((String) row[0]);
            
            if (row[1] != null) {
                metrics.setGpuUtilization(BigDecimal.valueOf(((Number) row[1]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            if (row[2] != null) {
                metrics.setMemoryUtilization(BigDecimal.valueOf(((Number) row[2]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            if (row[3] != null) {
                metrics.setTemperature(BigDecimal.valueOf(((Number) row[3]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            if (row[4] != null) {
                metrics.setPowerDraw(BigDecimal.valueOf(((Number) row[4]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            if (row[5] != null) {
                metrics.setFanSpeed(BigDecimal.valueOf(((Number) row[5]).doubleValue())
                    .setScale(1, RoundingMode.HALF_UP));
            }
            metrics.setTimestamp((LocalDateTime) row[6]);
            
            return metrics;
        }).collect(Collectors.toList());
    }

    /**
     * 활동 유형 결정
     */
    private String determineActivityType(String resourceType) {
        switch (resourceType) {
            case "FULL_GPU":
                return "GPU_ALLOCATION";
            case "MIG_INSTANCE":
                return "MIG_ALLOCATION";
            case "SHARED_GPU":
                return "SHARED_GPU_ALLOCATION";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * 활동 설명 생성
     */
    private String buildActivityDescription(Object[] row) {
        String resourceType = (String) row[0];
        String target = (String) row[1];
        String namespace = (String) row[5];
        String podName = (String) row[6];
        String status = (String) row[4];
        
        String action = "ALLOCATED".equals(status) ? "할당" : "해제";
        
        return String.format("%s가 %s/%s에 %s되었습니다", 
            resourceType, namespace, podName, action);
    }
}