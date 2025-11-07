package com.softsuave.crud.dto;

import lombok.Data;

// A DTO (Data Transfer Object) to safely transfer data to/from the API
@Data
public class StudentResponseDTO {
    private String name;
    private String branch;
    private float percentage;
    // We can expose the resume title, but maybe not the whole file path
    private String resumeTitle;
}
