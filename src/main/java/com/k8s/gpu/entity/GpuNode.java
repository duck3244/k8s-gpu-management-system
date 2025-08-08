package com.k8s.gpu.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GPU 노드 엔티티
 */
@Entity
@Table(name = "GPU_NODES")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class GpuNode extends BaseEntity {

    @Id
    @Column(name = "NODE_ID", length = 50)
    private String nodeId;

    @Column(name = "NODE_NAME", length = 100, nullable = false)
    private String nodeName;

    @Column(name = "CLUSTER_NAME", length = 50)
    private String clusterName;

    @Column(name = "NODE_IP", length = 15)
    private String nodeIp;

    @Column(name = "TOTAL_GPUS")
    private Integer totalGpus = 0;

    @Column(name = "AVAILABLE_GPUS")
    private Integer availableGpus = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "NODE_STATUS", length = 20)
    private NodeStatus nodeStatus = NodeStatus.ACTIVE;

    @Column(name = "KUBERNETES_VERSION", length = 20)
    private String kubernetesVersion;

    @Column(name = "DOCKER_VERSION", length = 20)
    private String dockerVersion;

    @Column(name = "NVIDIA_DRIVER_VERSION", length = 20)
    private String nvidiaDriverVersion;

    @Lob
    @Column(name = "NODE_LABELS")
    private String nodeLabels;

    @Lob
    @Column(name = "TAINTS")
    private String taints;

    // 연관관계
    @OneToMany(mappedBy = "gpuNode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GpuDevice> gpuDevices;

    public enum NodeStatus {
        ACTIVE, INACTIVE, MAINTENANCE, FAILED
    }
}