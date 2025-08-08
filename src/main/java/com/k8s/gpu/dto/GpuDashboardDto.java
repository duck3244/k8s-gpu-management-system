package com.k8s.gpu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GPU 대시보드 DTO
 */
@Data
@ApiModel(description = "GPU 대시보드 정보")
public class GpuDashboardDto {

    @ApiModelProperty(value = "전체 통계")
    private OverallStats overallStats;

    @ApiModelProperty(value = "클러스터별 통계")
    private List<ClusterStats> clusterStats;

    @ApiModelProperty(value = "GPU 모델별 통계")
    private List<ModelStats> modelStats;

    @ApiModelProperty(value = "최근 사용량 추이")
    private List<UsageTrend> usageTrends;

    @ApiModelProperty(value = "활성 알림")
    private List<AlertInfo> activeAlerts;

    @ApiModelProperty(value = "최근 활동")
    private List<RecentActivity> recentActivities;

    /**
     * 전체 통계
     */
    @Data
    @ApiModel(description = "전체 GPU 통계")
    public static class OverallStats {
        
        @ApiModelProperty(value = "총 노드 수", example = "12")
        private Integer totalNodes;

        @ApiModelProperty(value = "활성 노드 수", example = "10")
        private Integer activeNodes;

        @ApiModelProperty(value = "총 GPU 수", example = "96")
        private Integer totalGpus;

        @ApiModelProperty(value = "사용 중인 GPU 수", example = "73")
        private Integer usedGpus;

        @ApiModelProperty(value = "사용 가능한 GPU 수", example = "23")
        private Integer availableGpus;

        @ApiModelProperty(value = "전체 GPU 사용률", example = "76.04")
        private BigDecimal overallUtilization;

        @ApiModelProperty(value = "평균 온도", example = "67.5")
        private BigDecimal averageTemperature;

        @ApiModelProperty(value = "총 전력 소모량 (W)", example = "18500.5")
        private BigDecimal totalPowerConsumption;

        @ApiModelProperty(value = "MIG 활성화된 GPU 수", example = "15")
        private Integer migEnabledGpus;

        @ApiModelProperty(value = "총 MIG 인스턴스 수", example = "42")
        private Integer totalMigInstances;
    }

    /**
     * 클러스터별 통계
     */
    @Data
    @ApiModel(description = "클러스터별 GPU 통계")
    public static class ClusterStats {
        
        @ApiModelProperty(value = "클러스터명", example = "gpu-cluster-prod")
        private String clusterName;

        @ApiModelProperty(value = "노드 수", example = "8")
        private Integer nodeCount;

        @ApiModelProperty(value = "총 GPU 수", example = "64")
        private Integer totalGpus;

        @ApiModelProperty(value = "사용 중인 GPU 수", example = "48")
        private Integer usedGpus;

        @ApiModelProperty(value = "사용률", example = "75.0")
        private BigDecimal utilizationRate;

        @ApiModelProperty(value = "상태", example = "HEALTHY")
        private String status;
    }

    /**
     * GPU 모델별 통계
     */
    @Data
    @ApiModel(description = "GPU 모델별 통계")
    public static class ModelStats {
        
        @ApiModelProperty(value = "모델명", example = "GeForce RTX 4090")
        private String modelName;

        @ApiModelProperty(value = "아키텍처", example = "Ada Lovelace")
        private String architecture;

        @ApiModelProperty(value = "장비 수", example = "16")
        private Integer deviceCount;

        @ApiModelProperty(value = "사용 중인 장비 수", example = "12")
        private Integer usedDevices;

        @ApiModelProperty(value = "사용률", example = "75.0")
        private BigDecimal utilizationRate;

        @ApiModelProperty(value = "평균 온도", example = "68.5")
        private BigDecimal averageTemperature;

        @ApiModelProperty(value = "평균 전력 소모", example = "285.7")
        private BigDecimal averagePowerConsumption;
    }

    /**
     * 사용량 추이
     */
    @Data
    @ApiModel(description = "GPU 사용량 추이")
    public static class UsageTrend {
        
        @ApiModelProperty(value = "시간", example = "2025-08-01T10:00:00")
        private LocalDateTime timestamp;

        @ApiModelProperty(value = "전체 사용률", example = "76.5")
        private BigDecimal overallUtilization;

        @ApiModelProperty(value = "메모리 사용률", example = "68.2")
        private BigDecimal memoryUtilization;

        @ApiModelProperty(value = "평균 온도", example = "67.8")
        private BigDecimal averageTemperature;

        @ApiModelProperty(value = "총 전력 소모", example = "18245.3")
        private BigDecimal totalPowerConsumption;
    }

    /**
     * 알림 정보
     */
    @Data
    @ApiModel(description = "알림 정보")
    public static class AlertInfo {
        
        @ApiModelProperty(value = "알림 ID", example = "ALERT_20250801_001")
        private String alertId;

        @ApiModelProperty(value = "알림 유형", example = "TEMPERATURE")
        private String alertType;

        @ApiModelProperty(value = "심각도", example = "HIGH")
        private String severity;

        @ApiModelProperty(value = "대상 유형", example = "DEVICE")
        private String targetType;

        @ApiModelProperty(value = "대상 ID", example = "gpu-001")
        private String targetId;

        @ApiModelProperty(value = "메시지", example = "GPU 온도가 임계값을 초과했습니다")
        private String message;

        @ApiModelProperty(value = "발생 시간", example = "2025-08-01T14:30:00")
        private LocalDateTime createdDate;

        @ApiModelProperty(value = "상태", example = "ACTIVE")
        private String status;
    }

    /**
     * 최근 활동
     */
    @Data
    @ApiModel(description = "최근 활동")
    public static class RecentActivity {
        
        @ApiModelProperty(value = "활동 유형", example = "GPU_ALLOCATION")
        private String activityType;

        @ApiModelProperty(value = "설명", example = "GPU가 pod 'training-job-001'에 할당되었습니다")
        private String description;

        @ApiModelProperty(value = "대상", example = "gpu-device-001")
        private String target;

        @ApiModelProperty(value = "사용자", example = "ai-team")
        private String user;

        @ApiModelProperty(value = "시간", example = "2025-08-01T15:45:00")
        private LocalDateTime timestamp;

        @ApiModelProperty(value = "상태", example = "SUCCESS")
        private String status;
    }

    /**
     * 성능 메트릭
     */
    @Data
    @ApiModel(description = "성능 메트릭")
    public static class PerformanceMetrics {
        
        @ApiModelProperty(value = "장비 ID", example = "gpu-device-001")
        private String deviceId;

        @ApiModelProperty(value = "GPU 사용률", example = "85.7")
        private BigDecimal gpuUtilization;

        @ApiModelProperty(value = "메모리 사용률", example = "73.2")
        private BigDecimal memoryUtilization;

        @ApiModelProperty(value = "온도", example = "72.5")
        private BigDecimal temperature;

        @ApiModelProperty(value = "전력 소모", example = "320.5")
        private BigDecimal powerDraw;

        @ApiModelProperty(value = "팬 속도", example = "65.0")
        private BigDecimal fanSpeed;

        @ApiModelProperty(value = "측정 시간", example = "2025-08-01T16:00:00")
        private LocalDateTime timestamp;
    }

    /**
     * 비용 분석
     */
    @Data
    @ApiModel(description = "비용 분석")
    public static class CostAnalysis {
        
        @ApiModelProperty(value = "네임스페이스", example = "ai-training")
        private String namespace;

        @ApiModelProperty(value = "팀 ID", example = "ml-team")
        private String teamId;

        @ApiModelProperty(value = "총 사용 시간", example = "1248.5")
        private BigDecimal totalUsageHours;

        @ApiModelProperty(value = "총 비용", example = "2497.50")
        private BigDecimal totalCost;

        @ApiModelProperty(value = "시간당 평균 비용", example = "2.0")
        private BigDecimal averageCostPerHour;

        @ApiModelProperty(value = "기간", example = "지난 30일")
        private String period;
    }
}