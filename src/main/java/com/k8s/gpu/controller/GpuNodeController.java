package com.k8s.gpu.controller;

import com.k8s.gpu.dto.GpuNodeDto;
import com.k8s.gpu.entity.GpuNode;
import com.k8s.gpu.service.GpuNodeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * GPU 노드 관리 컨트롤러
 */
@RestController
@RequestMapping("/v1/gpu-nodes")
@RequiredArgsConstructor
@Slf4j
@Validated
@Api(tags = "GPU 노드 관리", description = "Kubernetes GPU 노드 관리 API")
public class GpuNodeController {

    private final GpuNodeService gpuNodeService;

    /**
     * GPU 노드 목록 조회
     */
    @GetMapping
    @ApiOperation(value = "GPU 노드 목록 조회", notes = "검색 조건에 따른 GPU 노드 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<Page<GpuNodeDto>> getGpuNodes(
            @ApiParam("검색 조건") @ModelAttribute GpuNodeDto.SearchCondition condition) {
        
        log.info("GPU 노드 목록 조회 요청 - condition: {}", condition);
        
        Page<GpuNodeDto> result = gpuNodeService.getGpuNodes(condition);
        
        log.info("GPU 노드 목록 조회 완료 - 총 {}개, 페이지 {}/{}", 
                result.getTotalElements(), result.getNumber() + 1, result.getTotalPages());
        
        return ResponseEntity.ok(result);
    }

    /**
     * GPU 노드 상세 조회
     */
    @GetMapping("/{nodeId}")
    @ApiOperation(value = "GPU 노드 상세 조회", notes = "특정 GPU 노드의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 404, message = "노드를 찾을 수 없음"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<GpuNodeDto> getGpuNode(
            @ApiParam(value = "노드 ID", required = true) @PathVariable String nodeId) {
        
        log.info("GPU 노드 상세 조회 요청 - nodeId: {}", nodeId);
        
        GpuNodeDto result = gpuNodeService.getGpuNode(nodeId);
        
        log.info("GPU 노드 상세 조회 완료 - nodeId: {}", nodeId);
        
        return ResponseEntity.ok(result);
    }

    /**
     * GPU 노드 등록
     */
    @PostMapping
    @ApiOperation(value = "GPU 노드 등록", notes = "새로운 GPU 노드를 등록합니다.")
    @ApiResponses({
        @ApiResponse(code = 201, message = "등록 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 409, message = "중복된 데이터"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<GpuNodeDto> createGpuNode(
            @ApiParam(value = "GPU 노드 정보", required = true) 
            @Valid @RequestBody GpuNodeDto.Request request) {
        
        log.info("GPU 노드 등록 요청 - request: {}", request);
        
        GpuNodeDto result = gpuNodeService.createGpuNode(request);
        
        log.info("GPU 노드 등록 완료 - nodeId: {}", result.getNodeId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * GPU 노드 수정
     */
    @PutMapping("/{nodeId}")
    @ApiOperation(value = "GPU 노드 수정", notes = "기존 GPU 노드 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "수정 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청"),
        @ApiResponse(code = 404, message = "노드를 찾을 수 없음"),
        @ApiResponse(code = 409, message = "중복된 데이터"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<GpuNodeDto> updateGpuNode(
            @ApiParam(value = "노드 ID", required = true) @PathVariable String nodeId,
            @ApiParam(value = "GPU 노드 정보", required = true) 
            @Valid @RequestBody GpuNodeDto.Request request) {
        
        log.info("GPU 노드 수정 요청 - nodeId: {}, request: {}", nodeId, request);
        
        GpuNodeDto result = gpuNodeService.updateGpuNode(nodeId, request);
        
        log.info("GPU 노드 수정 완료 - nodeId: {}", nodeId);
        
        return ResponseEntity.ok(result);
    }

    /**
     * GPU 노드 삭제
     */
    @DeleteMapping("/{nodeId}")
    @ApiOperation(value = "GPU 노드 삭제", notes = "GPU 노드를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(code = 204, message = "삭제 성공"),
        @ApiResponse(code = 404, message = "노드를 찾을 수 없음"),
        @ApiResponse(code = 409, message = "삭제할 수 없는 상태"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<Void> deleteGpuNode(
            @ApiParam(value = "노드 ID", required = true) @PathVariable String nodeId) {
        
        log.info("GPU 노드 삭제 요청 - nodeId: {}", nodeId);
        
        gpuNodeService.deleteGpuNode(nodeId);
        
        log.info("GPU 노드 삭제 완료 - nodeId: {}", nodeId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 활성 노드 목록 조회
     */
    @GetMapping("/active")
    @ApiOperation(value = "활성 노드 목록 조회", notes = "활성 상태인 GPU 노드 목록을 조회합니다.")
    public ResponseEntity<List<GpuNodeDto>> getActiveNodes() {
        
        log.info("활성 GPU 노드 목록 조회 요청");
        
        List<GpuNodeDto> result = gpuNodeService.getActiveNodes();
        
        log.info("활성 GPU 노드 목록 조회 완료 - 총 {}개", result.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 사용 가능한 GPU가 있는 노드 조회
     */
    @GetMapping("/available")
    @ApiOperation(value = "사용 가능한 GPU 노드 조회", notes = "사용 가능한 GPU가 있는 노드 목록을 조회합니다.")
    public ResponseEntity<List<GpuNodeDto>> getNodesWithAvailableGpus() {
        
        log.info("사용 가능한 GPU 노드 조회 요청");
        
        List<GpuNodeDto> result = gpuNodeService.getNodesWithAvailableGpus();
        
        log.info("사용 가능한 GPU 노드 조회 완료 - 총 {}개", result.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 클러스터별 노드 조회
     */
    @GetMapping("/cluster/{clusterName}")
    @ApiOperation(value = "클러스터별 노드 조회", notes = "특정 클러스터의 GPU 노드 목록을 조회합니다.")
    public ResponseEntity<List<GpuNodeDto>> getNodesByCluster(
            @ApiParam(value = "클러스터명", required = true) @PathVariable String clusterName) {
        
        log.info("클러스터별 노드 조회 요청 - clusterName: {}", clusterName);
        
        List<GpuNodeDto> result = gpuNodeService.getNodesByCluster(clusterName);
        
        log.info("클러스터별 노드 조회 완료 - clusterName: {}, 총 {}개", clusterName, result.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 최근 변경된 노드 조회
     */
    @GetMapping("/recent")
    @ApiOperation(value = "최근 변경된 노드 조회", notes = "최근 변경된 GPU 노드 목록을 조회합니다.")
    public ResponseEntity<List<GpuNodeDto>> getRecentlyUpdatedNodes(
            @ApiParam(value = "조회 시간(시간)", defaultValue = "24") 
            @RequestParam(defaultValue = "24") int hours) {
        
        log.info("최근 변경된 노드 조회 요청 - hours: {}", hours);
        
        List<GpuNodeDto> result = gpuNodeService.getRecentlyUpdatedNodes(hours);
        
        log.info("최근 변경된 노드 조회 완료 - hours: {}, 총 {}개", hours, result.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 노드 상태 변경
     */
    @PatchMapping("/{nodeId}/status")
    @ApiOperation(value = "노드 상태 변경", notes = "GPU 노드의 상태를 변경합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "상태 변경 성공"),
        @ApiResponse(code = 404, message = "노드를 찾을 수 없음"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<GpuNodeDto> updateNodeStatus(
            @ApiParam(value = "노드 ID", required = true) @PathVariable String nodeId,
            @ApiParam(value = "변경할 상태", required = true) 
            @RequestParam GpuNode.NodeStatus status) {
        
        log.info("노드 상태 변경 요청 - nodeId: {}, status: {}", nodeId, status);
        
        GpuNodeDto result = gpuNodeService.updateNodeStatus(nodeId, status);
        
        log.info("노드 상태 변경 완료 - nodeId: {}, status: {}", nodeId, status);
        
        return ResponseEntity.ok(result);
    }
}