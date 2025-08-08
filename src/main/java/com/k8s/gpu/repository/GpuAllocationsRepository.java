package com.k8s.gpu.repository;

import com.k8s.gpu.entity.GpuAllocations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpuAllocationsRepository extends JpaRepository<GpuAllocations, String> {

    /**
     * 최근 활동 조회 (Oracle 호환)
     */
    @Query("SELECT a.resourceType, a.allocatedResource, a.userId, " +
           "a.allocationTime, a.status, a.namespace, a.podName " +
           "FROM GpuAllocations a " +
           "ORDER BY a.allocationTime DESC")
    List<Object[]> getRecentActivities(Pageable pageable);

    /**
     * 기본 메서드에서 Pageable 사용
     */
    default List<Object[]> getRecentActivities(int limit) {
        return getRecentActivities(Pageable.ofSize(limit));
    }

    /**
     * 만료된 할당 조회
     */
    @Query("SELECT a FROM GpuAllocations a " +
           "WHERE a.status = 'ALLOCATED' " +
           "AND a.plannedReleaseTime < CURRENT_TIMESTAMP")
    List<GpuAllocations> findExpiredAllocations();

    /**
     * 네임스페이스별 할당 통계
     */
    @Query("SELECT a.namespace, COUNT(a), SUM(a.totalCost) " +
           "FROM GpuAllocations a " +
           "WHERE a.status = 'ALLOCATED' " +
           "GROUP BY a.namespace")
    List<Object[]> getNamespaceAllocationStats();
}