package com.coding.controller;

import com.coding.dto.request.CreateTagRequest;
import com.coding.dto.request.UpdateTagRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.TagResponse;
import com.coding.service.ITagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Tag", description = "Endpoints for managing product tags and classifications")
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    @Operation(summary = "Create a new tag")
    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> createTag(
            @Valid @RequestBody CreateTagRequest request) {
        TagResponse response = this.tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<TagResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tag created successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get tag by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> readTagById(
            @PathVariable UUID id) {
        TagResponse response = this.tagService.readTagById(id);
        return ResponseEntity.ok(
                ApiResponse.<TagResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tag retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get all tags")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> readAllTags() {
        List<TagResponse> response = this.tagService.readAllTags();
        return ResponseEntity.ok(
                ApiResponse.<List<TagResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tags retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Update tag by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTagRequest request) {
        TagResponse response = this.tagService.updateTag(id, request);
        return ResponseEntity.ok(
                ApiResponse.<TagResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tag updated successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Delete tag by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTagById(
            @PathVariable UUID id) {
        this.tagService.deleteTagById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tag deleted successfully")
                        .build()
        );
    }

}
