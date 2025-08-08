package com.k8s.gpu.controller;

import com.k8s.gpu.dto.GpuDashboardDto;
import com.k8s.gpu.service.GpuDashboardService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GPU 대시보드 컨트롤러
 */
@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "GPU 대시보드", description = "GPU 클러스터 대시보드 API")
public class GpuDashboardController {

    private final GpuDashboardService dashboardService;

    /**
     * 대시보드 전체 데이터 조회
     */
    @GetMapping
    @ApiOperation(value = "대시보드 전체 데이터 조회", notes = "GPU 클러스터의 전체 대시보드 데이터를 조회합니다.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "조회 성공"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<GpuDashboardDto> getDashboardData() {
        log.info("대시보드 전체 데이터 조회 요청");
        
        GpuDashboardDto result = dashboardService.getDashboardData();
        
        log.info("대시보드 전체 데이터 조회 완료");
        return ResponseEntity.ok(result);
    }

    /**
     * 전체 통계 조회
     */
    @GetMapping("/stats/overall")
    @ApiOperation(value = "전체 GPU 통계 조회", notes = "GPU 클러스터의 전체 통계를 조회합니다.")
    public ResponseEntity<GpuDashboardDto.OverallStats> getOverallStats() {
        log.info("전체 GPU 통계 조회 요청");
        
        GpuDashboardDto.OverallStats result = dashboardService.getOverallStats();
        
        log.info("전체 GPU 통계 조회 완료");
        return ResponseEntity.ok(result);
    }

    /**
     * 클러스터별 통계 조회
     */
    @GetMapping("/stats/clusters")
    @ApiOperation(value = "클러스터별 통계 조회", notes = "클러스터별 GPU 통계를 조회합니다.")
    public ResponseEntity<List<GpuDashboardDto.ClusterStats>> getClusterStats() {
        log.info("클러스터별 통계 조회 요청");
        
        List<GpuDashboardDto.ClusterStats> result = dashboardService.getClusterStats();
        
        log.info("클러스터별 통계 조회 완료 - {} 클러스터", result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * GPU 모델별 통계 조회
     */
    @GetMapping("/stats/models")
    @ApiOperation(value = "GPU 모델별 통계 조회", notes = "GPU 모델별 통계를 조회합니다.")
    public ResponseEntity<List<GpuDashboardDto.ModelStats>> getModelStats() {
        log.info("GPU 모델별 통계 조회 요청");
        
        List<GpuDashboardDto.ModelStats> result = dashboardService.getModelStats();
        
        log.info("GPU 모델별 통계 조회 완료 - {} 모델", result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 사용량 추이 조회
     */
    @GetMapping("/trends/usage")
    @ApiOperation(value = "GPU 사용량 추이 조회", notes = "지정된 시간 동안의 GPU 사용량 추이를 조회합니다.")
    public ResponseEntity<List<GpuDashboardDto.UsageTrend>> getUsageTrends(
            @ApiParam(value = "조회 시간(시간)", defaultValue = "24") 
            @RequestParam(defaultValue = "24") int hours) {
        
        log.info("GPU 사용량 추이 조회 요청 - hours: {}", hours);
        
        List<GpuDashboardDto.UsageTrend> result = dashboardService.getUsageTrends(hours);
        
        log.info("GPU 사용량 추이 조회 완료 - {} 포인트", result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 활성 알림 조회
     */
    @GetMapping("/alerts/active")
    @ApiOperation(value = "활성 알림 조회", notes = "현재 활성 상태인 알림 목록을 조회합니다.")
    public ResponseEntity<List<GpuDashboardDto.AlertInfo>> getActiveAlerts() {
        log.info("활성 알림 조회 요청");
        
        List<GpuDashboardDto.AlertInfo> result = dashboardService.getActiveAlerts();
        
        log.info("활성 알림 조회 완료 - {} 건", result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 최근 활동 조회
     */
    @GetMapping("/activities/recent")
    @ApiOperation(value = "최근 활동 조회", notes = "최근 GPU 관련 활동 내역을 조회합니다.")
    public ResponseEntity<List<GpuDashboardDto.RecentActivity>> getRecentActivities(
            @ApiParam(value = "조회 건수", defaultValue = "50") 
            @RequestParam(defaultValue = "50") int limit) {
        
        log.info("최근 활동 조회 요청 - limit: {}", limit);
        
        List<GpuDashboardDto.RecentActivity> result = dashboardService.getRecentActivities(limit);
        
        log.info("최근 활동 조회 완료 - {} 건", result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 실시간 성능 메트릭 조회
     */
    @GetMapping("/metrics/realtime")
    @ApiOperation(value = "실시간 성능 메트릭 조회", notes = "모든 GPU의 실시간 성능 메트릭을 조회합니다.")
    public ResponseEntity<List<GpuDashboardDto.PerformanceMetrics>> getRealtimeMetrics() {
        log.info("실시간 성능 메트릭 조회 요청");
        
        List<GpuDashboardDto.PerformanceMetrics> result = dashboardService.getRealtimeMetrics();
        
        log.info("실시간 성능 메트릭 조회 완료 - {} 장비", result.size());
        return ResponseEntity.ok(result);
    }
}