package com.k8s.gpu.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SpringDoc OpenAPI 3 설정
 * Swagger 2에서 OpenAPI 3로 업그레이드
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI k8sGpuManagementOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                    new Server().url("http://localhost:8080" + contextPath).description("개발 서버"),
                    new Server().url("https://api.k8s-gpu.company.com" + contextPath).description("운영 서버")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("K8s GPU Management System API")
                .description("Kubernetes 환경의 GPU 리소스 관리 시스템 API")
                .version("1.0.0")
                .contact(new Contact()
                    .name("K8s GPU Team")
                    .email("k8s-gpu-team@company.com")
                    .url("https://github.com/company/k8s-gpu-management"))
                .license(new License()
                    .name("Apache License Version 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }
}

// OpenAPI 어노테이션 사용 예시를 위한 컨트롤러 개선 샘플
/*
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/gpu-nodes")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "GPU 노드 관리", description = "Kubernetes GPU 노드 관리 API")
public class GpuNodeController {

    @GetMapping("/{nodeId}")
    @Operation(summary = "GPU 노드 상세 조회", description = "특정 GPU 노드의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = GpuNodeDto.class))),
        @ApiResponse(responseCode = "404", description = "노드를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<GpuNodeDto> getGpuNode(
            @Parameter(description = "노드 ID", required = true, example = "node-gpu-001") 
            @PathVariable String nodeId) {
        // 구현...
    }
}
*/