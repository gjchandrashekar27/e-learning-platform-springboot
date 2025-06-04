package com.jnana.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SectionDto {

    @NotBlank(message = "* Title is required")
    @Size(min = 5, max = 50, message = "* Title should be between 5 and 50 characters")
    private String title;

    private MultipartFile video;

    private MultipartFile notes;

    @NotNull(message = "* Course ID is required")
    private Long courseId;
}
