package com.k8s.gpu.service;

import com.k8s.gpu.dto.GpuNodeDto;
import com.k8s.gpu.entity.GpuNode;
import com.k8s.gpu.exception.ResourceNotFoundException;
import com.k8s.gpu.mapper.GpuNodeMapper;
import com.k8s.gpu.repository.GpuNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GPU 노드 서비스 테스트")
class GpuNodeServiceTest {

    @Mock
    private GpuNodeRepository gpuNodeRepository;

    @Mock
    private GpuNodeMapper gpuNodeMapper;

    @InjectMocks
    private GpuNodeService gpuNodeService;

    private GpuNode testNode;
    private GpuNodeDto testNodeDto;
    private GpuNodeDto.Request testRequest;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        testNode = new GpuNode();
        testNode.setNodeId("test-node-001");
        testNode.setNodeName("test-gpu-worker-01");
        testNode.setClusterName("test-cluster");
        testNode.setNodeIp("192.168.1.100");
        testNode.setTotalGpus(8);
        testNode.setAvailableGpus(3);
        testNode.setNodeStatus(GpuNode.NodeStatus.ACTIVE);
        testNode.setCreatedDate(LocalDateTime.now());
        testNode.setUpdatedDate(LocalDateTime.now());

        testNodeDto = new GpuNodeDto();
        testNodeDto.setNodeId("test-node-001");
        testNodeDto.setNodeName("test-gpu-worker-01");
        testNodeDto.setClusterName("test-cluster");
        testNodeDto.setNodeIp("192.168.1.100");
        testNodeDto.setTotalGpus(8);
        testNodeDto.setAvailableGpus(3);
        testNodeDto.setNodeStatus(GpuNode.NodeStatus.ACTIVE);

        testRequest = new GpuNodeDto.Request();
        testRequest.setNodeId("test-node-001");
        testRequest.setNodeName("test-gpu-worker-01");
        testRequest.setClusterName("test-cluster");
        testRequest.setNodeIp("192.168.1.100");
        testRequest.setTotalGpus(8);
        testRequest.setAvailableGpus(3);
        testRequest.setNodeStatus(GpuNode.NodeStatus.ACTIVE);
    }

    @Test
    @DisplayName("GPU 노드 목록 조회 성공")
    void getGpuNodes_Success() {
        // Given
        GpuNodeDto.SearchCondition condition = new GpuNodeDto.SearchCondition();
        condition.setPage(0);
        condition.setSize(20);
        condition.setSortBy("createdDate");
        condition.setSortDirection("DESC");

        List<GpuNode> nodeList = Arrays.asList(testNode);
        Page<GpuNode> nodePage = new PageImpl<>(nodeList, PageRequest.of(0, 20), 1);

        when(gpuNodeRepository.findBySearchConditions(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(nodePage);
        when(gpuNodeMapper.toDto(testNode)).thenReturn(testNodeDto);

        // When
        Page<GpuNodeDto> result = gpuNodeService.getGpuNodes(condition);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testNodeDto.getNodeId(), result.getContent().get(0).getNodeId());
        verify(gpuNodeRepository).findBySearchConditions(any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("GPU 노드 상세 조회 성공")
    void getGpuNode_Success() {
        // Given
        String nodeId = "test-node-001";
        when(gpuNodeRepository.findById(nodeId)).thenReturn(Optional.of(testNode));
        when(gpuNodeMapper.toDto(testNode)).thenReturn(testNodeDto);

        // When
        GpuNodeDto result = gpuNodeService.getGpuNode(nodeId);

        // Then
        assertNotNull(result);
        assertEquals(nodeId, result.getNodeId());
        verify(gpuNodeRepository).findById(nodeId);
        verify(gpuNodeMapper).toDto(testNode);
    }

    @Test
    @DisplayName("GPU 노드 상세 조회 실패 - 노드를 찾을 수 없음")
    void getGpuNode_NotFound() {
        // Given
        String nodeId = "non-existent-node";
        when(gpuNodeRepository.findById(nodeId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> gpuNodeService.getGpuNode(nodeId));
        
        assertTrue(exception.getMessage().contains("GPU 노드를 찾을 수 없습니다"));
        verify(gpuNodeRepository).findById(nodeId);
        verify(gpuNodeMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("GPU 노드 등록 성공")
    void createGpuNode_Success() {
        // Given
        when(gpuNodeRepository.existsById(testRequest.getNodeId())).thenReturn(false);
        when(gpuNodeRepository.findByNodeName(testRequest.getNodeName())).thenReturn(Optional.empty());
        when(gpuNodeMapper.toEntity(testRequest)).thenReturn(testNode);
        when(gpuNodeRepository.save(testNode)).thenReturn(testNode);
        when(gpuNodeMapper.toDto(testNode)).thenReturn(testNodeDto);

        // When
        GpuNodeDto result = gpuNodeService.createGpuNode(testRequest);

        // Then
        assertNotNull(result);
        assertEquals(testRequest.getNodeId(), result.getNodeId());
        verify(gpuNodeRepository).existsById(testRequest.getNodeId());
        verify(gpuNodeRepository).findByNodeName(testRequest.getNodeName());
        verify(gpuNodeRepository).save(testNode);
    }

    @Test
    @DisplayName("GPU 노드 등록 실패 - 중복된 노드 ID")
    void createGpuNode_DuplicateNodeId() {
        // Given
        when(gpuNodeRepository.existsById(testRequest.getNodeId())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> gpuNodeService.createGpuNode(testRequest));

        assertTrue(exception.getMessage().contains("이미 존재하는 노드 ID입니다"));
        verify(gpuNodeRepository).existsById(testRequest.getNodeId());
        verify(gpuNodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("GPU 노드 상태 변경 성공")
    void updateNodeStatus_Success() {
        // Given
        String nodeId = "test-node-001";
        GpuNode.NodeStatus newStatus = GpuNode.NodeStatus.MAINTENANCE;
        
        when(gpuNodeRepository.findById(nodeId)).thenReturn(Optional.of(testNode));
        when(gpuNodeRepository.save(testNode)).thenReturn(testNode);
        when(gpuNodeMapper.toDto(testNode)).thenReturn(testNodeDto);

        // When
        GpuNodeDto result = gpuNodeService.updateNodeStatus(nodeId, newStatus);

        // Then
        assertNotNull(result);
        assertEquals(newStatus, testNode.getNodeStatus());
        assertEquals(0, testNode.getAvailableGpus()); // MAINTENANCE 상태에서는 available GPU가 0
        verify(gpuNodeRepository).findById(nodeId);
        verify(gpuNodeRepository).save(testNode);
    }

    @Test
    @DisplayName("활성 노드 목록 조회 성공")
    void getActiveNodes_Success() {
        // Given
        List<GpuNode> activeNodes = Arrays.asList(testNode);
        List<GpuNodeDto> activeNodeDtos = Arrays.asList(testNodeDto);
        
        when(gpuNodeRepository.findActiveNodes()).thenReturn(activeNodes);
        when(gpuNodeMapper.toDtoList(activeNodes)).thenReturn(activeNodeDtos);

        // When
        List<GpuNodeDto> result = gpuNodeService.getActiveNodes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNodeDto.getNodeId(), result.get(0).getNodeId());
        verify(gpuNodeRepository).findActiveNodes();
    }
}