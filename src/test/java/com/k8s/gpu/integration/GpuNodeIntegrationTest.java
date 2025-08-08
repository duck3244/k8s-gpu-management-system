package com.k8s.gpu.integration;

import com.k8s.gpu.dto.GpuNodeDto;
import com.k8s.gpu.entity.GpuNode;
import com.k8s.gpu.repository.GpuNodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("GPU 노드 API 통합 테스트")
class GpuNodeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GpuNodeRepository gpuNodeRepository;

    @BeforeEach
    void setUp() {
        gpuNodeRepository.deleteAll();
    }

    @Test
    @DisplayName("GPU 노드 생성부터 조회까지 전체 플로우 테스트")
    void gpuNodeFullFlow_Success() throws Exception {
        // 1. GPU 노드 생성
        GpuNodeDto.Request createRequest = new GpuNodeDto.Request();
        createRequest.setNodeId("integration-test-node");
        createRequest.setNodeName("integration-test-worker");
        createRequest.setClusterName("test-cluster");
        createRequest.setNodeIp("192.168.1.200");
        createRequest.setTotalGpus(4);
        createRequest.setAvailableGpus(4);
        createRequest.setNodeStatus(GpuNode.NodeStatus.ACTIVE);

        mockMvc.perform(post("/v1/gpu-nodes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nodeId").value("integration-test-node"));

        // 2. 생성된 노드 조회
        mockMvc.perform(get("/v1/gpu-nodes/integration-test-node"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeId").value("integration-test-node"))
                .andExpect(jsonPath("$.nodeName").value("integration-test-worker"))
                .andExpect(jsonPath("$.totalGpus").value(4));

        // 3. 노드 목록에서 확인
        mockMvc.perform(get("/v1/gpu-nodes")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nodeId").value("integration-test-node"))
                .andExpect(jsonPath("$.totalElements").value(1));

        // 4. 노드 상태 변경
        mockMvc.perform(patch("/v1/gpu-nodes/integration-test-node/status")
                .param("status", "MAINTENANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeStatus").value("MAINTENANCE"))
                .andExpect(jsonPath("$.availableGpus").value(0));

        // 5. 노드 삭제
        mockMvc.perform(delete("/v1/gpu-nodes/integration-test-node"))
                .andExpect(status().isNoContent());

        // 6. 삭제 확인
        mockMvc.perform(get("/v1/gpu-nodes/integration-test-node"))
                .andExpect(status().isNotFound());
    }
}