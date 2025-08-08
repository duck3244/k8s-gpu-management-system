package com.k8s.gpu.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 에러 응답 DTO
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "에러 응답")
public class ErrorResponse {

    @ApiModelProperty(value = "오류 발생 시간", example = "2025-08-01T15:30:00")
    private LocalDateTime timestamp;

    @ApiModelProperty(value = "HTTP 상태 코드", example = "404")
    private int status;

    @ApiModelProperty(value = "오류 유형", example = "Not Found")
    private String error;

    @ApiModelProperty(value = "오류 메시지", example = "GPU 노드를 찾을 수 없습니다")
    private String message;

    @ApiModelProperty(value = "상세 오류 정보")
    private Map<String, Object> details;

    @ApiModelProperty(value = "요청 경로", example = "/api/v1/gpu-nodes/invalid-id")
    private String path;
}