package com.coding.service;

import com.coding.dto.request.CreateTagRequest;
import com.coding.dto.request.UpdateTagRequest;
import com.coding.dto.response.TagResponse;

import java.util.List;
import java.util.UUID;

public interface ITagService {

    TagResponse createTag(CreateTagRequest request);

    TagResponse readTagById(UUID tagId);

    List<TagResponse> readAllTags();

    TagResponse updateTag(UUID tagId, UpdateTagRequest request);

    void deleteTagById(UUID tagId);

}
