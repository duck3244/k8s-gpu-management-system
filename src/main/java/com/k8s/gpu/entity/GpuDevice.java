package com.k8s.gpu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * GPU 장비 엔티티
 */
@Entity
@Table(name = "GPU_DEVICES")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class GpuDevice extends BaseEntity {

    @Id
    @Column(name = "DEVICE_ID", length = 50)
    private String deviceId;

    @Column(name = "NODE_ID", length = 50, nullable = false)
    private String nodeId;

    @Column(name = "MODEL_ID", length = 20, nullable = false)
    private String modelId;

    @Column(name = "DEVICE_INDEX", nullable = false)
    private Integer deviceIndex;

    @Column(name = "SERIAL_NUMBER", length = 50)
    private String serialNumber;

    @Column(name = "PCI_ADDRESS", length = 20, nullable = false)
    private String pciAddress;

    @Column(name = "GPU_UUID", length = 100, nullable = false, unique = true)
    private String gpuUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "DEVICE_STATUS", length = 20)
    private DeviceStatus deviceStatus = DeviceStatus.ACTIVE;

    @Column(name = "CURRENT_TEMP_C", precision = 5, scale = 2)
    private BigDecimal currentTempC;

    @Column(name = "MAX_TEMP_C", precision = 5, scale = 2)
    private BigDecimal maxTempC = BigDecimal.valueOf(83);

    @Column(name = "CURRENT_POWER_W", precision = 6, scale = 2)
    private BigDecimal currentPowerW;

    @Column(name = "MAX_POWER_W", precision = 6, scale = 2)
    private BigDecimal maxPowerW;

    @Column(name = "DRIVER_VERSION", length = 20)
    private String driverVersion;

    @Column(name = "FIRMWARE_VERSION", length = 20)
    private String firmwareVersion;

    @Column(name = "VBIOS_VERSION", length = 20)
    private String vbiosVersion;

    @Column(name = "INSTALLATION_DATE")
    private LocalDate installationDate;

    @Column(name = "LAST_MAINTENANCE_DATE")
    private LocalDate lastMaintenanceDate;

    @Column(name = "WARRANTY_EXPIRY_DATE")
    private LocalDate warrantyExpiryDate;

    @Column(name = "PURCHASE_COST", precision = 10, scale = 2)
    private BigDecimal purchaseCost;

    @Column(name = "DEPRECIATION_MONTHS")
    private Integer depreciationMonths = 36;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NODE_ID", insertable = false, updatable = false)
    private GpuNode gpuNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODEL_ID", insertable = false, updatable = false)
    private GpuModel gpuModel;

    public enum DeviceStatus {
        ACTIVE, INACTIVE, MAINTENANCE, FAILED, MIG_ENABLED
    }
}