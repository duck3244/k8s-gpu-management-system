package com.k8s.gpu.service;

import com.k8s.gpu.dto.GpuNodeDto;
import com.k8s.gpu.entity.GpuNode;
import com.k8s.gpu.exception.ResourceNotFoundException;
import com.k8s.gpu.mapper.GpuNodeMapper;
import com.k8s.gpu.repository.GpuNodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GPU 노드 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GpuNodeService {

    private final GpuNodeRepository gpuNodeRepository;
    private final GpuNodeMapper gpuNodeMapper;

    /**
     * GPU 노드 목록 조회 (검색 조건 포함)
     */
    public Page<GpuNodeDto> getGpuNodes(GpuNodeDto.SearchCondition condition) {
        log.debug("GPU 노드 목록 조회 - 조건: {}", condition);

        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(condition.getSortDirection()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            condition.getSortBy()
        );

        Pageable pageable = PageRequest.of(condition.getPage(), condition.getSize(), sort);

        Page<GpuNode> nodes = gpuNodeRepository.findBySearchConditions(
            condition.getNodeName(),
            condition.getClusterName(),
            condition.getNodeStatus(),
            condition.getFromDate(),
            condition.getToDate(),
            pageable
        );

        return nodes.map(gpuNodeMapper::toDto);
    }

    /**
     * GPU 노드 상세 조회
     */
    public GpuNodeDto getGpuNode(String nodeId) {
        log.debug("GPU 노드 상세 조회 - nodeId: {}", nodeId);

        GpuNode node = gpuNodeRepository.findById(nodeId)
            .orElseThrow(() -> new ResourceNotFoundException("GPU 노드를 찾을 수 없습니다. nodeId: " + nodeId));

        return gpuNodeMapper.toDto(node);
    }

    /**
     * GPU 노드 등록
     */
    @Transactional
    public GpuNodeDto createGpuNode(GpuNodeDto.Request request) {
        log.info("GPU 노드 등록 - request: {}", request);

        // 중복 체크
        if (gpuNodeRepository.existsById(request.getNodeId())) {
            throw new IllegalArgumentException("이미 존재하는 노드 ID입니다: " + request.getNodeId());
        }

        if (gpuNodeRepository.findByNodeName(request.getNodeName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 노드명입니다: " + request.getNodeName());
        }

        GpuNode node = gpuNodeMapper.toEntity(request);
        GpuNode savedNode = gpuNodeRepository.save(node);

        log.info("GPU 노드 등록 완료 - nodeId: {}", savedNode.getNodeId());
        return gpuNodeMapper.toDto(savedNode);
    }

    /**
     * GPU 노드 수정
     */
    @Transactional
    public GpuNodeDto updateGpuNode(String nodeId, GpuNodeDto.Request request) {
        log.info("GPU 노드 수정 - nodeId: {}, request: {}", nodeId, request);

        GpuNode existingNode = gpuNodeRepository.findById(nodeId)
            .orElseThrow(() -> new ResourceNotFoundException("GPU 노드를 찾을 수 없습니다. nodeId: " + nodeId));

        // 노드명 중복 체크 (자신 제외)
        gpuNodeRepository.findByNodeName(request.getNodeName())
            .filter(node -> !node.getNodeId().equals(nodeId))
            .ifPresent(node -> {
                throw new IllegalArgumentException("이미 존재하는 노드명입니다: " + request.getNodeName());
            });

        // 엔티티 업데이트
        gpuNodeMapper.updateEntity(request, existingNode);
        GpuNode updatedNode = gpuNodeRepository.save(existingNode);

        log.info("GPU 노드 수정 완료 - nodeId: {}", nodeId);
        return gpuNodeMapper.toDto(updatedNode);
    }

    /**
     * GPU 노드 삭제
     */
    @Transactional
    public void deleteGpuNode(String nodeId) {
        log.info("GPU 노드 삭제 - nodeId: {}", nodeId);

        GpuNode node = gpuNodeRepository.findById(nodeId)
            .orElseThrow(() -> new ResourceNotFoundException("GPU 노드를 찾을 수 없습니다. nodeId: " + nodeId));

        // GPU 장비가 있는지 확인
        if (node.getGpuDevices() != null && !node.getGpuDevices().isEmpty()) {
            throw new IllegalStateException("GPU 장비가 있는 노드는 삭제할 수 없습니다. nodeId: " + nodeId);
        }

        gpuNodeRepository.delete(node);
        log.info("GPU 노드 삭제 완료 - nodeId: {}", nodeId);
    }

    /**
     * 활성 노드 목록 조회
     */
    public List<GpuNodeDto> getActiveNodes() {
        log.debug("활성 GPU 노드 목록 조회");

        List<GpuNode> activeNodes = gpuNodeRepository.findActiveNodes();
        return gpuNodeMapper.toDtoList(activeNodes);
    }

    /**
     * 사용 가능한 GPU가 있는 노드 조회
     */
    public List<GpuNodeDto> getNodesWithAvailableGpus() {
        log.debug("사용 가능한 GPU가 있는 노드 조회");

        List<GpuNode> availableNodes = gpuNodeRepository.findNodesWithAvailableGpus();
        return gpuNodeMapper.toDtoList(availableNodes);
    }

    /**
     * 클러스터별 노드 조회
     */
    public List<GpuNodeDto> getNodesByCluster(String clusterName) {
        log.debug("클러스터별 노드 조회 - clusterName: {}", clusterName);

        List<GpuNode> nodes = gpuNodeRepository.findByClusterName(clusterName);
        return gpuNodeMapper.toDtoList(nodes);
    }

    /**
     * 최근 변경된 노드 조회
     */
    public List<GpuNodeDto> getRecentlyUpdatedNodes(int hours) {
        log.debug("최근 {}시간 내 변경된 노드 조회", hours);

        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<GpuNode> recentNodes = gpuNodeRepository.findRecentlyUpdatedNodes(since);
        return gpuNodeMapper.toDtoList(recentNodes);
    }

    /**
     * 노드 상태 변경
     */
    @Transactional
    public GpuNodeDto updateNodeStatus(String nodeId, GpuNode.NodeStatus newStatus) {
        log.info("노드 상태 변경 - nodeId: {}, newStatus: {}", nodeId, newStatus);

        GpuNode node = gpuNodeRepository.findById(nodeId)
            .orElseThrow(() -> new ResourceNotFoundException("GPU 노드를 찾을 수 없습니다. nodeId: " + nodeId));

        GpuNode.NodeStatus oldStatus = node.getNodeStatus();
        node.setNodeStatus(newStatus);
        
        // 상태가 INACTIVE나 MAINTENANCE로 변경되면 available GPU를 0으로 설정
        if (newStatus == GpuNode.NodeStatus.INACTIVE || newStatus == GpuNode.NodeStatus.MAINTENANCE) {
            node.setAvailableGpus(0);
        }

        GpuNode updatedNode = gpuNodeRepository.save(node);
        
        log.info("노드 상태 변경 완료 - nodeId: {}, {} -> {}", nodeId, oldStatus, newStatus);
        return gpuNodeMapper.toDto(updatedNode);
    }
}