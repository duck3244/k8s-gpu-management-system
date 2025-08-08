package com.k8s.gpu.repository;

import com.k8s.gpu.entity.GpuUsageMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GpuUsageMetricsRepository extends JpaRepository<GpuUsageMetrics, String> {

    @Query("SELECT AVG(m.temperatureC), SUM(m.powerDrawW) " +
           "FROM GpuUsageMetrics m " +
           "WHERE m.timestamp >= :since")
    Object[] getLatestMetricsStatistics(@Param("since") LocalDateTime since);

    default Object[] getLatestMetricsStatistics() {
        return getLatestMetricsStatistics(LocalDateTime.now().minusHours(1));
    }

    @Query("SELECT AVG(m.temperatureC), AVG(m.powerDrawW) " +
           "FROM GpuUsageMetrics m " +
           "JOIN GpuDevice d ON m.deviceId = d.deviceId " +
           "JOIN d.gpuModel gm ON d.modelId = gm.modelId " +
           "WHERE gm.modelName = :modelName " +
           "AND m.timestamp >= :since")
    Object[] getModelAverageMetrics(@Param("modelName") String modelName, @Param("since") LocalDateTime since);

    default Object[] getModelAverageMetrics(String modelName) {
        return getModelAverageMetrics(modelName, LocalDateTime.now().minusHours(1));
    }

    @Query("SELECT DATE_TRUNC('hour', m.timestamp), " +
           "AVG(m.gpuUtilizationPct), AVG(m.memoryUtilizationPct), " +
           "AVG(m.temperatureC), SUM(m.powerDrawW) " +
           "FROM GpuUsageMetrics m " +
           "WHERE m.timestamp >= :since " +
           "GROUP BY DATE_TRUNC('hour', m.timestamp) " +
           "ORDER BY DATE_TRUNC('hour', m.timestamp)")
    List<Object[]> getUsageTrends(@Param("since") LocalDateTime since);

    @Query("SELECT m.deviceId, m.gpuUtilizationPct, m.memoryUtilizationPct, " +
           "m.temperatureC, m.powerDrawW, m.fanSpeedPct, m.timestamp " +
           "FROM GpuUsageMetrics m " +
           "WHERE m.timestamp = (SELECT MAX(m2.timestamp) FROM GpuUsageMetrics m2 WHERE m2.deviceId = m.deviceId)")
    List<Object[]> getLatestDeviceMetrics();
}