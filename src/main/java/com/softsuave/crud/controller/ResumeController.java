package com.softsuave.crud.controller;

import com.softsuave.crud.dto.ResumeRequestDTO;
import com.softsuave.crud.dto.ResumeResponseDTO;
import com.softsuave.crud.entity.Resume;
import com.softsuave.crud.service.ResumeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

/**
 * ----------------------- RESUME CONTROLLER -----------------------
 * This controller handles all HTTP requests related to Resume data.
 *
 *  @RestController  → Tells Spring this is a REST controller.
 *  @RequestMapping("/resumes") → Sets the base path for all endpoints here.
 *
 * Example Endpoints:
 *   GET    /resumes           → Get all resumes
 *   GET    /resumes/{id}      → Get a resume by ID
 *   POST   /resumes/{id}/add  → Add a resume for a specific student
 */
@RestController
@RequestMapping("/resumes")
public class ResumeController {

    // Logger for this controller — logs will go to controller.log (based on your logback.xml)
    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    @Autowired
    private ResumeService resumeService;

    /**
     * ---------------------- GET ALL RESUMES ----------------------
     * Endpoint: GET /resumes
     * Access: Only ADMIN can access this.
     * Purpose: Fetch all resumes stored in the database.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ResumeResponseDTO>> getAllResumes() {
        logger.info("GET /resumes - Request received to fetch all resumes");

        List<ResumeResponseDTO> resumes = resumeService.getAllResumes();

        logger.info("GET /resumes - Returning {} resumes", resumes.size());
        return ResponseEntity.ok(resumes);
    }

    /**
     * ---------------------- GET RESUME BY ID ----------------------
     * Endpoint: GET /resumes/{id}
     * Access: USER or ADMIN can access.
     * Purpose: Fetch a specific resume by its ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ResumeResponseDTO> getResumeById(@PathVariable Long id) {
        logger.info("GET /resumes/{} - Request received", id);

        ResumeResponseDTO resume = resumeService.getResumeById(id);

        logger.info("GET /resumes/{} - Resume retrieved successfully with title: {}", id, resume.getResumeTitle());
        return ResponseEntity.ok(resume);
    }

    /**
     * ---------------------- ADD RESUME ----------------------
     * Endpoint: POST /resumes/{id}/add
     * Access: USER or ADMIN can add a resume.
     * Purpose: Add a resume for a specific student (identified by ID).
     */
    @PostMapping("/{id}/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(code = CREATED)
    public ResponseEntity<ResumeResponseDTO> addResume(
            @PathVariable Long id,
            @RequestBody ResumeRequestDTO resumeRequestDTO) {

        logger.info("POST /resumes/{}/add - Request to add resume for student ID {}", id, id);

        ResumeResponseDTO createdResume = resumeService.addResume(id, resumeRequestDTO);

        logger.info("POST /resumes/{}/add - Resume added successfully with title: {}", id, createdResume.getResumeTitle());
        return ResponseEntity.ok(createdResume);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public String uploadResume(@RequestParam("studentId") Long studentId,
                               @RequestParam("title") String title,
                               @RequestParam("file") MultipartFile file){
        resumeService.storeFileAndAddResume(studentId,title,file);
        return "file uploaded and has been saved";
    }

    @GetMapping("/download/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadMyResume(Authentication authentication){

        String username=authentication.getName();

        Resume resume=resumeService.getResumeForAuthenticatedUser(username);


        String headerValue="attachment; filename=\""+resume.getFileName()+"\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resume.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,headerValue)
                .body(resume.getFileData());
    }
}
