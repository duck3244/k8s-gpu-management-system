package com.k8s.gpu.controller;

import com.k8s.gpu.dto.GpuNodeDto;
import com.k8s.gpu.entity.GpuNode;
import com.k8s.gpu.service.GpuNodeService;

import main.java.com.k8s.gpu.controller.GpuNodeController;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GpuNodeController.class)
@DisplayName("GPU 노드 컨트롤러 테스트")
class GpuNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GpuNodeService gpuNodeService;

    @Autowired
    private ObjectMapper objectMapper;

    private GpuNodeDto testNodeDto;

    @BeforeEach
    void setUp() {
        testNodeDto = new GpuNodeDto();
        testNodeDto.setNodeId("test-node-001");
        testNodeDto.setNodeName("test-gpu-worker-01");
        testNodeDto.setClusterName("test-cluster");
        testNodeDto.setNodeIp("192.168.1.100");
        testNodeDto.setTotalGpus(8);
        testNodeDto.setAvailableGpus(3);
        testNodeDto.setNodeStatus(GpuNode.NodeStatus.ACTIVE);
        testNodeDto.setCreatedDate(LocalDateTime.now());
        testNodeDto.setUpdatedDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("GPU 노드 목록 조회 API 성공")
    void getGpuNodes_Success() throws Exception {
        // Given
        Page<GpuNodeDto> nodePage = new PageImpl<>(Arrays.asList(testNodeDto));
        when(gpuNodeService.getGpuNodes(any())).thenReturn(nodePage);

        // When & Then
        mockMvc.perform(get("/v1/gpu-nodes")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].nodeId").value("test-node-001"))
                .andExpect(jsonPath("$.content[0].nodeName").value("test-gpu-worker-01"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GPU 노드 상세 조회 API 성공")
    void getGpuNode_Success() throws Exception {
        // Given
        String nodeId = "test-node-001";
        when(gpuNodeService.getGpuNode(nodeId)).thenReturn(testNodeDto);

        // When & Then
        mockMvc.perform(get("/v1/gpu-nodes/{nodeId}", nodeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodeId").value(nodeId))
                .andExpect(jsonPath("$.nodeName").value("test-gpu-worker-01"))
                .andExpect(jsonPath("$.totalGpus").value(8))
                .andExpect(jsonPath("$.availableGpus").value(3));
    }

    @Test
    @DisplayName("GPU 노드 등록 API 성공")
    void createGpuNode_Success() throws Exception {
        // Given
        GpuNodeDto.Request request = new GpuNodeDto.Request();
        request.setNodeId("test-node-001");
        request.setNodeName("test-gpu-worker-01");
        request.setClusterName("test-cluster");
        request.setNodeIp("192.168.1.100");
        request.setTotalGpus(8);
        request.setAvailableGpus(3);
        request.setNodeStatus(GpuNode.NodeStatus.ACTIVE);

        when(gpuNodeService.createGpuNode(any())).thenReturn(testNodeDto);

        // When & Then
        mockMvc.perform(post("/v1/gpu-nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodeId").value("test-node-001"))
                .andExpect(jsonPath("$.nodeName").value("test-gpu-worker-01"));
    }

    @Test
    @DisplayName("GPU 노드 등록 API 실패 - 유효성 검증 오류")
    void createGpuNode_ValidationError() throws Exception {
        // Given
        GpuNodeDto.Request invalidRequest = new GpuNodeDto.Request();
        // nodeId와 nodeName을 비워둠 (필수 필드)
        
        // When & Then
        mockMvc.perform(post("/v1/gpu-nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값 검증에 실패했습니다"));
    }

    @Test
    @DisplayName("GPU 노드 상태 변경 API 성공")
    void updateNodeStatus_Success() throws Exception {
        // Given
        String nodeId = "test-node-001";
        GpuNode.NodeStatus newStatus = GpuNode.NodeStatus.MAINTENANCE;
        
        testNodeDto.setNodeStatus(newStatus);
        testNodeDto.setAvailableGpus(0);
        
        when(gpuNodeService.updateNodeStatus(eq(nodeId), eq(newStatus))).thenReturn(testNodeDto);

        // When & Then
        mockMvc.perform(patch("/v1/gpu-nodes/{nodeId}/status", nodeId)
                .param("status", newStatus.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodeId").value(nodeId))
                .andExpect(jsonPath("$.nodeStatus").value("MAINTENANCE"))
                .andExpect(jsonPath("$.availableGpus").value(0));
    }
}