package com.rlrio.voting.mapper;

import com.rlrio.voting.controller.dto.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PageMapper {
    PageMapper INSTANCE = Mappers.getMapper(PageMapper.class);

    @Mapping(target = "pageNumber", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "totalSize", source = "totalElements")
    PageResponse toPageResponse(Page source);
}
