package com.k8s.gpu.repository;

import com.k8s.gpu.entity.GpuDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpuDeviceRepository extends JpaRepository<GpuDevice, String> {

    List<GpuDevice> findByDeviceStatusIn(List<GpuDevice.DeviceStatus> statuses);

    @Query("SELECT COUNT(d), " +
           "SUM(CASE WHEN d.deviceStatus = 'ACTIVE' OR d.deviceStatus = 'MIG_ENABLED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN d.deviceStatus = 'ACTIVE' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN d.deviceStatus = 'MIG_ENABLED' THEN 1 ELSE 0 END), " +
           "0 " +
           "FROM GpuDevice d")
    Object[] getOverallGpuStatistics();

    @Query("SELECT gm.modelName, gm.architecture, COUNT(d), " +
           "SUM(CASE WHEN d.deviceStatus = 'ACTIVE' OR d.deviceStatus = 'MIG_ENABLED' THEN 1 ELSE 0 END) " +
           "FROM GpuDevice d " +
           "JOIN d.gpuModel gm " +
           "GROUP BY gm.modelName, gm.architecture")
    List<Object[]> getModelStatistics();
}