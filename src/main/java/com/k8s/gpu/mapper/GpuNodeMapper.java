package com.k8s.gpu.mapper;

import com.k8s.gpu.dto.GpuNodeDto;
import com.k8s.gpu.entity.GpuNode;
import org.mapstruct.*;

import java.util.List;

/**
 * GPU 노드 매퍼
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GpuNodeMapper {

    /**
     * Entity -> DTO 변환
     */
    GpuNodeDto toDto(GpuNode entity);

    /**
     * Entity List -> DTO List 변환
     */
    List<GpuNodeDto> toDtoList(List<GpuNode> entities);

    /**
     * Request DTO -> Entity 변환
     */
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "gpuDevices", ignore = true)
    GpuNode toEntity(GpuNodeDto.Request request);

    /**
     * Request DTO로 기존 Entity 업데이트
     */
    @Mapping(target = "nodeId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "gpuDevices", ignore = true)
    void updateEntity(GpuNodeDto.Request request, @MappingTarget GpuNode entity);
}