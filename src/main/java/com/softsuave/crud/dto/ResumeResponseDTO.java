package com.softsuave.crud.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeResponseDTO {
    private String resumeTitle;
    private byte[] fileData;
}
