package com.softsuave.crud.dto;

import com.softsuave.crud.entity.Resume;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRequestDTO {
    private String name;
    private String branch;
    private float percentage;
    private Resume resume;
}
