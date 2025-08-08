package com.k8s.gpu.repository;

import com.k8s.gpu.entity.GpuNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * GPU 노드 Repository
 */
@Repository
public interface GpuNodeRepository extends JpaRepository<GpuNode, String> {

    /**
     * 노드명으로 검색
     */
    Optional<GpuNode> findByNodeName(String nodeName);

    /**
     * 클러스터별 노드 조회
     */
    List<GpuNode> findByClusterName(String clusterName);

    /**
     * 노드 상태별 조회
     */
    List<GpuNode> findByNodeStatus(GpuNode.NodeStatus nodeStatus);

    /**
     * 활성 노드 조회
     */
    @Query("SELECT n FROM GpuNode n WHERE n.nodeStatus = 'ACTIVE' ORDER BY n.nodeName")
    List<GpuNode> findActiveNodes();

    /**
     * 사용 가능한 GPU가 있는 노드 조회
     */
    @Query("SELECT n FROM GpuNode n WHERE n.nodeStatus = 'ACTIVE' AND n.availableGpus > 0 ORDER BY n.availableGpus DESC")
    List<GpuNode> findNodesWithAvailableGpus();

    /**
     * 검색 조건으로 노드 조회 (페이징)
     */
    @Query("SELECT n FROM GpuNode n WHERE " +
           "(:nodeName IS NULL OR LOWER(n.nodeName) LIKE LOWER(CONCAT('%', :nodeName, '%'))) AND " +
           "(:clusterName IS NULL OR n.clusterName = :clusterName) AND " +
           "(:nodeStatus IS NULL OR n.nodeStatus = :nodeStatus) AND " +
           "(:fromDate IS NULL OR n.createdDate >= :fromDate) AND " +
           "(:toDate IS NULL OR n.createdDate <= :toDate) " +
           "ORDER BY n.createdDate DESC")
    Page<GpuNode> findBySearchConditions(
            @Param("nodeName") String nodeName,
            @Param("clusterName") String clusterName,
            @Param("nodeStatus") GpuNode.NodeStatus nodeStatus,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    /**
     * 클러스터별 노드 통계
     */
    @Query("SELECT n.clusterName, COUNT(n), SUM(n.totalGpus), SUM(n.availableGpus) " +
           "FROM GpuNode n " +
           "WHERE n.nodeStatus = 'ACTIVE' " +
           "GROUP BY n.clusterName")
    List<Object[]> getClusterStatistics();

    /**
     * 노드 상태별 통계
     */
    @Query("SELECT n.nodeStatus, COUNT(n) FROM GpuNode n GROUP BY n.nodeStatus")
    List<Object[]> getNodeStatusStatistics();

    /**
     * 최근 변경된 노드 조회
     */
    @Query("SELECT n FROM GpuNode n WHERE n.updatedDate >= :since ORDER BY n.updatedDate DESC")
    List<GpuNode> findRecentlyUpdatedNodes(@Param("since") LocalDateTime since);

    /**
     * GPU 사용률이 높은 노드 조회
     */
    @Query("SELECT n FROM GpuNode n WHERE n.nodeStatus = 'ACTIVE' AND " +
           "(CAST(n.totalGpus - n.availableGpus AS DOUBLE) / CAST(n.totalGpus AS DOUBLE)) > :utilizationThreshold " +
           "ORDER BY (CAST(n.totalGpus - n.availableGpus AS DOUBLE) / CAST(n.totalGpus AS DOUBLE)) DESC")
    List<GpuNode> findHighUtilizationNodes(@Param("utilizationThreshold") double utilizationThreshold);
}