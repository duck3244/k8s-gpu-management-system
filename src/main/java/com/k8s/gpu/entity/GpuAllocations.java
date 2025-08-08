package com.k8s.gpu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GPU_ALLOCATIONS")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class GpuAllocations extends BaseEntity {

    @Id
    @Column(name = "ALLOCATION_ID", length = 50)
    private String allocationId;

    @Column(name = "NAMESPACE", length = 50, nullable = false)
    private String namespace;

    @Column(name = "POD_NAME", length = 100, nullable = false)
    private String podName;

    @Column(name = "CONTAINER_NAME", length = 100)
    private String containerName;

    @Column(name = "WORKLOAD_TYPE", length = 30)
    private String workloadType;

    @Enumerated(EnumType.STRING)
    @Column(name = "RESOURCE_TYPE", length = 20, nullable = false)
    private ResourceType resourceType;

    @Column(name = "ALLOCATED_RESOURCE", length = 50, nullable = false)
    private String allocatedResource;

    @Column(name = "REQUESTED_MEMORY_GB")
    private Integer requestedMemoryGb;

    @Column(name = "ALLOCATED_MEMORY_GB")
    private Integer allocatedMemoryGb;

    @Column(name = "PRIORITY_CLASS", length = 20)
    private String priorityClass = "normal";

    @Column(name = "ALLOCATION_TIME")
    private LocalDateTime allocationTime;

    @Column(name = "PLANNED_RELEASE_TIME")
    private LocalDateTime plannedReleaseTime;

    @Column(name = "RELEASE_TIME")
    private LocalDateTime releaseTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20)
    private AllocationStatus status = AllocationStatus.ALLOCATED;

    @Column(name = "COST_PER_HOUR", precision = 8, scale = 4)
    private BigDecimal costPerHour;

    @Column(name = "TOTAL_COST", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "USER_ID", length = 50)
    private String userId;

    @Column(name = "TEAM_ID", length = 50)
    private String teamId;

    @Column(name = "PROJECT_ID", length = 50)
    private String projectId;

    public enum ResourceType {
        FULL_GPU, MIG_INSTANCE, SHARED_GPU
    }

    public enum AllocationStatus {
        PENDING, ALLOCATED, RELEASED, FAILED, EXPIRED
    }
}