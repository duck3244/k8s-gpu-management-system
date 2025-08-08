package com.k8s.gpu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SYSTEM_ALERTS")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class SystemAlerts extends BaseEntity {

    @Id
    @Column(name = "ALERT_ID", length = 50)
    private String alertId;

    @Column(name = "RULE_ID", length = 50)
    private String ruleId;

    @Column(name = "ALERT_TYPE", length = 30, nullable = false)
    private String alertType;

    @Column(name = "SEVERITY", length = 10, nullable = false)
    private String severity;

    @Column(name = "TARGET_TYPE", length = 20)
    private String targetType;

    @Column(name = "TARGET_ID", length = 50)
    private String targetId;

    @Column(name = "MESSAGE", length = 500, nullable = false)
    private String message;

    @Lob
    @Column(name = "DETAILS")
    private String details;

    @Column(name = "METRIC_VALUE", precision = 10, scale = 2)
    private BigDecimal metricValue;

    @Column(name = "THRESHOLD_VALUE", precision = 10, scale = 2)
    private BigDecimal thresholdValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20)
    private AlertStatus status = AlertStatus.ACTIVE;

    @Column(name = "ACKNOWLEDGED_DATE")
    private LocalDateTime acknowledgedDate;

    @Column(name = "ACKNOWLEDGED_BY", length = 50)
    private String acknowledgedBy;

    @Column(name = "RESOLVED_DATE")
    private LocalDateTime resolvedDate;

    @Column(name = "RESOLVED_BY", length = 50)
    private String resolvedBy;

    public enum AlertStatus {
        ACTIVE, ACKNOWLEDGED, RESOLVED, SUPPRESSED
    }
}