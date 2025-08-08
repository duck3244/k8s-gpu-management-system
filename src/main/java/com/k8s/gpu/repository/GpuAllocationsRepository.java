package com.k8s.gpu.repository;

import com.k8s.gpu.entity.GpuAllocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpuAllocationsRepository extends JpaRepository<GpuAllocations, String> {

    @Query("SELECT a.resourceType, a.allocatedResource, a.userId, " +
           "a.allocationTime, a.status, a.namespace, a.podName " +
           "FROM GpuAllocations a " +
           "ORDER BY a.allocationTime DESC " +
           "LIMIT :limit")
    List<Object[]> getRecentActivities(@Param("limit") int limit);
}