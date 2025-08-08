package com.k8s.gpu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * GPU 모델 엔티티
 */
@Entity
@Table(name = "GPU_MODELS")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class GpuModel extends BaseEntity {

    @Id
    @Column(name = "MODEL_ID", length = 20)
    private String modelId;

    @Column(name = "MODEL_NAME", length = 50, nullable = false)
    private String modelName;

    @Column(name = "MANUFACTURER", length = 20, nullable = false)
    private String manufacturer = "NVIDIA";

    @Column(name = "ARCHITECTURE", length = 30, nullable = false)
    private String architecture;

    @Column(name = "MEMORY_GB", nullable = false)
    private Integer memoryGb;

    @Column(name = "CUDA_CORES")
    private Integer cudaCores;

    @Column(name = "TENSOR_CORES")
    private Integer tensorCores;

    @Column(name = "RT_CORES")
    private Integer rtCores = 0;

    @Column(name = "BASE_CLOCK_MHZ")
    private Integer baseClockMhz;

    @Column(name = "BOOST_CLOCK_MHZ")
    private Integer boostClockMhz;

    @Column(name = "MEMORY_BANDWIDTH_GBPS", precision = 6, scale = 1)
    private BigDecimal memoryBandwidthGbps;

    @Column(name = "MEMORY_TYPE", length = 20)
    private String memoryType;

    @Column(name = "POWER_CONSUMPTION_W", nullable = false)
    private Integer powerConsumptionW;

    @Column(name = "PCIE_GENERATION", length = 10)
    private String pcieGeneration;

    @Column(name = "MIG_SUPPORT", length = 1)
    private String migSupport = "N";

    @Column(name = "MAX_MIG_INSTANCES")
    private Integer maxMigInstances = 0;

    @Column(name = "COMPUTE_CAPABILITY", length = 10)
    private String computeCapability;

    @Column(name = "RELEASE_YEAR")
    private Integer releaseYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "MARKET_SEGMENT", length = 20)
    private MarketSegment marketSegment;

    @Column(name = "END_OF_LIFE_DATE")
    private LocalDate endOfLifeDate;

    public enum MarketSegment {
        Gaming, Professional, Datacenter
    }
}