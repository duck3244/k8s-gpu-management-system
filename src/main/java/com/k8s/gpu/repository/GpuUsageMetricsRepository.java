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

    /**
     * 최근 메트릭 통계 (Oracle 호환)
     */
    @Query("SELECT AVG(m.temperatureC), SUM(m.powerDrawW) " +
           "FROM GpuUsageMetrics m " +
           "WHERE m.timestamp >= :since")
    Object[] getLatestMetricsStatistics(@Param("since") LocalDateTime since);

    default Object[] getLatestMetricsStatistics() {
        return getLatestMetricsStatistics(LocalDateTime.now().minusHours(1));
    }

    /**
     * 모델별 평균 메트릭 (Oracle 호환)
     */
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

    /**
     * 사용량 추이 (Oracle 호환 - TRUNC 함수 사용)
     */
    @Query("SELECT TRUNC(m.timestamp, 'HH'), " +
           "AVG(m.gpuUtilizationPct), AVG(m.memoryUtilizationPct), " +
           "AVG(m.temperatureC), SUM(m.powerDrawW) " +
           "FROM GpuUsageMetrics m " +
           "WHERE m.timestamp >= :since " +
           "GROUP BY TRUNC(m.timestamp, 'HH') " +
           "ORDER BY TRUNC(m.timestamp, 'HH')")
    List<Object[]> getUsageTrends(@Param("since") LocalDateTime since);

    /**
     * 최신 장비별 메트릭 (Oracle 호환)
     */
    @Query("SELECT m.deviceId, m.gpuUtilizationPct, m.memoryUtilizationPct, " +
           "m.temperatureC, m.powerDrawW, m.fanSpeedPct, m.timestamp " +
           "FROM GpuUsageMetrics m " +
           "WHERE m.timestamp = (SELECT MAX(m2.timestamp) FROM GpuUsageMetrics m2 WHERE m2.deviceId = m.deviceId)")
    List<Object[]> getLatestDeviceMetrics();

    /**
     * 장비별 최근 N개 메트릭 조회
     */
    @Query(value = "SELECT * FROM (" +
                   "    SELECT m.*, ROW_NUMBER() OVER (PARTITION BY m.DEVICE_ID ORDER BY m.TIMESTAMP DESC) as rn " +
                   "    FROM GPU_USAGE_METRICS m " +
                   "    WHERE m.DEVICE_ID = :deviceId " +
                   ") WHERE rn <= :count", 
           nativeQuery = true)
    List<GpuUsageMetrics> getRecentMetricsByDevice(@Param("deviceId") String deviceId, @Param("count") int count);

    /**
     * 임계값 초과 메트릭 조회
     */
    @Query("SELECT m FROM GpuUsageMetrics m " +
           "WHERE m.timestamp >= :since " +
           "AND (m.temperatureC > :tempThreshold " +
           "     OR m.gpuUtilizationPct > :utilizationThreshold " +
           "     OR m.powerDrawW > :powerThreshold)")
    List<GpuUsageMetrics> getMetricsExceedingThresholds(
        @Param("since") LocalDateTime since,
        @Param("tempThreshold") Double tempThreshold,
        @Param("utilizationThreshold") Double utilizationThreshold,
        @Param("powerThreshold") Double powerThreshold
    );
}