package com.k8s.gpu.repository;

import com.k8s.gpu.entity.GpuNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("GPU 노드 Repository 테스트")
class GpuNodeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GpuNodeRepository gpuNodeRepository;

    private GpuNode testNode1;
    private GpuNode testNode2;

    @BeforeEach
    void setUp() {
        testNode1 = new GpuNode();
        testNode1.setNodeId("test-node-001");
        testNode1.setNodeName("test-gpu-worker-01");
        testNode1.setClusterName("test-cluster");
        testNode1.setNodeIp("192.168.1.100");
        testNode1.setTotalGpus(8);
        testNode1.setAvailableGpus(3);
        testNode1.setNodeStatus(GpuNode.NodeStatus.ACTIVE);

        testNode2 = new GpuNode();
        testNode2.setNodeId("test-node-002");
        testNode2.setNodeName("test-gpu-worker-02");
        testNode2.setClusterName("prod-cluster");
        testNode2.setNodeIp("192.168.1.101");
        testNode2.setTotalGpus(4);
        testNode2.setAvailableGpus(0);
        testNode2.setNodeStatus(GpuNode.NodeStatus.MAINTENANCE);

        entityManager.persistAndFlush(testNode1);
        entityManager.persistAndFlush(testNode2);
    }

    @Test
    @DisplayName("노드명으로 조회 성공")
    void findByNodeName_Success() {
        // When
        Optional<GpuNode> result = gpuNodeRepository.findByNodeName("test-gpu-worker-01");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test-node-001", result.get().getNodeId());
        assertEquals("test-gpu-worker-01", result.get().getNodeName());
    }

    @Test
    @DisplayName("클러스터별 노드 조회 성공")
    void findByClusterName_Success() {
        // When
        List<GpuNode> result = gpuNodeRepository.findByClusterName("test-cluster");

        // Then
        assertEquals(1, result.size());
        assertEquals("test-node-001", result.get(0).getNodeId());
    }

    @Test
    @DisplayName("활성 노드 조회 성공")
    void findActiveNodes_Success() {
        // When
        List<GpuNode> result = gpuNodeRepository.findActiveNodes();

        // Then
        assertEquals(1, result.size());
        assertEquals(GpuNode.NodeStatus.ACTIVE, result.get(0).getNodeStatus());
        assertEquals("test-node-001", result.get(0).getNodeId());
    }

    @Test
    @DisplayName("사용 가능한 GPU가 있는 노드 조회 성공")
    void findNodesWithAvailableGpus_Success() {
        // When
        List<GpuNode> result = gpuNodeRepository.findNodesWithAvailableGpus();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailableGpus() > 0);
        assertEquals("test-node-001", result.get(0).getNodeId());
    }

    @Test
    @DisplayName("검색 조건으로 노드 조회 성공")
    void findBySearchConditions_Success() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);

        // When
        Page<GpuNode> result = gpuNodeRepository.findBySearchConditions(
                "worker", // nodeName 부분 검색
                null,     // clusterName
                null,     // nodeStatus
                null,     // fromDate
                null,     // toDate
                pageRequest
        );

        // Then
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(node -> node.getNodeName().contains("worker")));
    }

    @Test
    @DisplayName("클러스터별 통계 조회 성공")
    void getClusterStatistics_Success() {
        // When
        List<Object[]> result = gpuNodeRepository.getClusterStatistics();

        // Then
        assertEquals(1, result.size()); // ACTIVE 노드만 포함
        Object[] stats = result.get(0);
        assertEquals("test-cluster", stats[0]);
        assertEquals(1L, stats[1]); // 노드 수
        assertEquals(8L, stats[2]); // 총 GPU 수
        assertEquals(3L, stats[3]); // 사용 가능한 GPU 수
    }

    @Test
    @DisplayName("최근 변경된 노드 조회 성공")
    void findRecentlyUpdatedNodes_Success() {
        // Given
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        // When
        List<GpuNode> result = gpuNodeRepository.findRecentlyUpdatedNodes(oneHourAgo);

        // Then
        assertEquals(2, result.size()); // 모든 노드가 최근에 생성됨
    }
}