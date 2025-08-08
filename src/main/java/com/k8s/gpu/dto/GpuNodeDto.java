package com.k8s.gpu.dto;

import com.k8s.gpu.entity.GpuNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * GPU 노드 DTO
 */
@Data
@ApiModel(description = "GPU 노드 정보")
public class GpuNodeDto {

    @ApiModelProperty(value = "노드 ID", example = "node-gpu-001", required = true)
    @NotBlank(message = "노드 ID는 필수입니다")
    @Size(max = 50, message = "노드 ID는 50자를 초과할 수 없습니다")
    private String nodeId;

    @ApiModelProperty(value = "노드명", example = "k8s-gpu-worker-01", required = true)
    @NotBlank(message = "노드명은 필수입니다")
    @Size(max = 100, message = "노드명은 100자를 초과할 수 없습니다")
    private String nodeName;

    @ApiModelProperty(value = "클러스터명", example = "gpu-cluster-prod")
    @Size(max = 50, message = "클러스터명은 50자를 초과할 수 없습니다")
    private String clusterName;

    @ApiModelProperty(value = "노드 IP", example = "192.168.1.100")
    @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", 
             message = "올바른 IP 주소 형식이 아닙니다")
    private String nodeIp;

    @ApiModelProperty(value = "총 GPU 수", example = "8")
    private Integer totalGpus = 0;

    @ApiModelProperty(value = "사용 가능한 GPU 수", example = "3")
    private Integer availableGpus = 0;

    @ApiModelProperty(value = "노드 상태", example = "ACTIVE", allowableValues = "ACTIVE,INACTIVE,MAINTENANCE,FAILED")
    @NotNull(message = "노드 상태는 필수입니다")
    private GpuNode.NodeStatus nodeStatus;

    @ApiModelProperty(value = "Kubernetes 버전", example = "v1.25.0")
    @Size(max = 20, message = "Kubernetes 버전은 20자를 초과할 수 없습니다")
    private String kubernetesVersion;

    @ApiModelProperty(value = "Docker 버전", example = "20.10.17")
    @Size(max = 20, message = "Docker 버전은 20자를 초과할 수 없습니다")
    private String dockerVersion;

    @ApiModelProperty(value = "NVIDIA 드라이버 버전", example = "525.105.17")
    @Size(max = 20, message = "NVIDIA 드라이버 버전은 20자를 초과할 수 없습니다")
    private String nvidiaDriverVersion;

    @ApiModelProperty(value = "노드 레이블 (JSON 형식)", example = "{\"gpu.type\":\"A100\",\"zone\":\"us-west-1a\"}")
    private String nodeLabels;

    @ApiModelProperty(value = "테인트 (JSON 형식)", example = "{\"nvidia.com/gpu\":\"NoSchedule\"}")
    private String taints;

    @ApiModelProperty(value = "생성일시", example = "2025-08-01T10:00:00")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "수정일시", example = "2025-08-01T15:30:00")
    private LocalDateTime updatedDate;

    /**
     * 요청 DTO (등록/수정용)
     */
    @Data
    @ApiModel(description = "GPU 노드 요청 정보")
    public static class Request {
        
        @ApiModelProperty(value = "노드 ID", example = "node-gpu-001", required = true)
        @NotBlank(message = "노드 ID는 필수입니다")
        @Size(max = 50, message = "노드 ID는 50자를 초과할 수 없습니다")
        private String nodeId;

        @ApiModelProperty(value = "노드명", example = "k8s-gpu-worker-01", required = true)
        @NotBlank(message = "노드명은 필수입니다")
        @Size(max = 100, message = "노드명은 100자를 초과할 수 없습니다")
        private String nodeName;

        @ApiModelProperty(value = "클러스터명", example = "gpu-cluster-prod")
        @Size(max = 50, message = "클러스터명은 50자를 초과할 수 없습니다")
        private String clusterName;

        @ApiModelProperty(value = "노드 IP", example = "192.168.1.100")
        @Pattern(regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", 
                 message = "올바른 IP 주소 형식이 아닙니다")
        private String nodeIp;

        @ApiModelProperty(value = "총 GPU 수", example = "8")
        private Integer totalGpus = 0;

        @ApiModelProperty(value = "사용 가능한 GPU 수", example = "3")
        private Integer availableGpus = 0;

        @ApiModelProperty(value = "노드 상태", example = "ACTIVE")
        @NotNull(message = "노드 상태는 필수입니다")
        private GpuNode.NodeStatus nodeStatus;

        @ApiModelProperty(value = "Kubernetes 버전", example = "v1.25.0")
        @Size(max = 20, message = "Kubernetes 버전은 20자를 초과할 수 없습니다")
        private String kubernetesVersion;

        @ApiModelProperty(value = "Docker 버전", example = "20.10.17")
        @Size(max = 20, message = "Docker 버전은 20자를 초과할 수 없습니다")
        private String dockerVersion;

        @ApiModelProperty(value = "NVIDIA 드라이버 버전", example = "525.105.17")
        @Size(max = 20, message = "NVIDIA 드라이버 버전은 20자를 초과할 수 없습니다")
        private String nvidiaDriverVersion;

        @ApiModelProperty(value = "노드 레이블 (JSON 형식)")
        private String nodeLabels;

        @ApiModelProperty(value = "테인트 (JSON 형식)")
        private String taints;
    }

    /**
     * 검색 조건 DTO
     */
    @Data
    @ApiModel(description = "GPU 노드 검색 조건")
    public static class SearchCondition {
        
        @ApiModelProperty(value = "노드명 (부분 검색)", example = "worker")
        private String nodeName;

        @ApiModelProperty(value = "클러스터명", example = "gpu-cluster-prod")
        private String clusterName;

        @ApiModelProperty(value = "노드 상태", example = "ACTIVE")
        private GpuNode.NodeStatus nodeStatus;

        @ApiModelProperty(value = "검색 시작일", example = "2025-08-01T00:00:00")
        private LocalDateTime fromDate;

        @ApiModelProperty(value = "검색 종료일", example = "2025-08-31T23:59:59")
        private LocalDateTime toDate;

        @ApiModelProperty(value = "페이지 번호 (0부터 시작)", example = "0")
        private int page = 0;

        @ApiModelProperty(value = "페이지 크기", example = "20")
        private int size = 20;

        @ApiModelProperty(value = "정렬 필드", example = "createdDate")
        private String sortBy = "createdDate";

        @ApiModelProperty(value = "정렬 방향", example = "DESC", allowableValues = "ASC,DESC")
        private String sortDirection = "DESC";
    }
}