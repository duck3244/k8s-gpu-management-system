package com.k8s.gpu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GPU_USAGE_METRICS")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class GpuUsageMetrics extends BaseEntity {

    @Id
    @Column(name = "METRIC_ID", length = 50)
    private String metricId;

    @Column(name = "DEVICE_ID", length = 50)
    private String deviceId;

    @Column(name = "MIG_ID", length = 50)
    private String migId;

    @Column(name = "ALLOCATION_ID", length = 50)
    private String allocationId;

    @Column(name = "GPU_UTILIZATION_PCT", precision = 5, scale = 2)
    private BigDecimal gpuUtilizationPct;

    @Column(name = "MEMORY_USED_MB")
    private Integer memoryUsedMb;

    @Column(name = "MEMORY_TOTAL_MB")
    private Integer memoryTotalMb;

    @Column(name = "MEMORY_UTILIZATION_PCT", precision = 5, scale = 2)
    private BigDecimal memoryUtilizationPct;

    @Column(name = "TEMPERATURE_C", precision = 5, scale = 2)
    private BigDecimal temperatureC;

    @Column(name = "POWER_DRAW_W", precision = 6, scale = 2)
    private BigDecimal powerDrawW;

    @Column(name = "FAN_SPEED_PCT", precision = 5, scale = 2)
    private BigDecimal fanSpeedPct;

    @Column(name = "CLOCK_GRAPHICS_MHZ")
    private Integer clockGraphicsMhz;

    @Column(name = "CLOCK_MEMORY_MHZ")
    private Integer clockMemoryMhz;

    @Column(name = "PCIE_TX_MBPS", precision = 8, scale = 2)
    private BigDecimal pcieTxMbps;

    @Column(name = "PCIE_RX_MBPS", precision = 8, scale = 2)
    private BigDecimal pcieRxMbps;

    @Column(name = "PROCESSES_COUNT")
    private Integer processesCount;

    @Column(name = "TIMESTAMP")
    private LocalDateTime timestamp;

    @Column(name = "COLLECTION_SOURCE", length = 20)
    private String collectionSource;
}