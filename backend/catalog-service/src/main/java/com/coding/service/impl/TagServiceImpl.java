package com.coding.service.impl;

import com.coding.dto.request.CreateTagRequest;
import com.coding.dto.request.UpdateTagRequest;
import com.coding.dto.response.TagResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.TagMapper;
import com.coding.model.Tag;
import com.coding.repository.TagRepository;
import com.coding.service.ITagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class TagServiceImpl implements ITagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagResponse createTag(CreateTagRequest request) {
        if (this.tagRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Tag already exists with name: " + request.name());
        }

        Tag tag = this.tagMapper.toEntity(request);
        Tag savedTag = this.tagRepository.save(tag);
        return this.tagMapper.toResponse(savedTag);
    }

    @Override
    public TagResponse readTagById(UUID tagId) {
        return this.tagRepository.findById(tagId)
                .map(this.tagMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + tagId));
    }

    @Override
    public List<TagResponse> readAllTags() {
        return this.tagRepository.findAll()
                .stream()
                .map(this.tagMapper::toResponse)
                .toList();
    }

    @Override
    public TagResponse updateTag(UUID tagId, UpdateTagRequest request) {
        Tag tag = this.tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with ID: " + tagId));

        if (!tag.getName().equalsIgnoreCase(request.name()) && this.tagRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Tag already exists with name: " + request.name());
        }

        this.tagMapper.updateTagFromRequest(request, tag);
        Tag updatedTag = this.tagRepository.saveAndFlush(tag);
        return this.tagMapper.toResponse(updatedTag);
    }

    @Override
    public void deleteTagById(UUID tagId) {
        if (!this.tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Cannot delete. Tag not found with ID: " + tagId);
        }
        this.tagRepository.deleteById(tagId);
    }

}
