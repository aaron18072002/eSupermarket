package com.coding.mapper;

import com.coding.dto.request.CreateTagRequest;
import com.coding.dto.request.UpdateTagRequest;
import com.coding.dto.response.TagResponse;
import com.coding.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    Tag toEntity(CreateTagRequest request);

    TagResponse toResponse(Tag tag);

    @Mapping(target = "id", ignore = true)
    void updateTagFromRequest(UpdateTagRequest request, @MappingTarget Tag tag);

}
