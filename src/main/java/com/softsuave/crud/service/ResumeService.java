package com.softsuave.crud.service;

import com.softsuave.crud.dto.ResumeRequestDTO;
import com.softsuave.crud.dto.ResumeResponseDTO;
import com.softsuave.crud.entity.Resume;
import com.softsuave.crud.entity.Student;
import com.softsuave.crud.entity.Users;
import com.softsuave.crud.exception.ResumeNotFoundException;
import com.softsuave.crud.exception.StudentNotFoundException;
import com.softsuave.crud.repository.ResumeRepository;
import com.softsuave.crud.repository.StudentRepository;

import com.softsuave.crud.repository.UserRepository;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File; // Memory: Use java.io.File for FileUtils
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


/**
 * ----------------------- RESUME SERVICE -----------------------
 *
 * This class handles all the *business logic* related to resumes.
 * It acts as the middle layer between the controller and repository:
 *
 *   Controller  →  Service (logic)  →  Repository (database)
 *
 * @Service - Marks this class as a Spring-managed service bean.
 */
@Service
public class ResumeService {

    // Logger for this service — logs will go to service.log (based on logback.xml)
    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    // Injects repository beans automatically using Spring's dependency injection
    @Autowired
    private ResumeRepository resumeRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private UserRepository userRepository;



    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * ---------------------- GET ALL RESUMES ----------------------
     * Purpose: Retrieve a list of all resumes from the database.
     */
    public List<ResumeResponseDTO> getAllResumes() {
        logger.info("Fetching all resumes from database");

        List<Resume> resumesList = resumeRepo.findAll();
        logger.debug("Fetched {} resumes from repository", resumesList.size());

        return resumesList.stream()
                .map(this::mapResumetoDTO)
                .collect(Collectors.toList());
    }

    /**
     * ---------------------- GET RESUME BY ID ----------------------
     * Purpose: Retrieve a single resume by its ID.
     * Throws: ResumeNotFoundException if no record is found.
     */
    public ResumeResponseDTO getResumeById(Long id) {
        logger.info("Fetching resume by ID: {}", id);

        Resume resume = resumeRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("Resume not found with ID: {}", id);
                    return new ResumeNotFoundException("Resume not found with ID: " + id);
                });

        logger.info("Successfully fetched resume with ID: {}", id);
        return mapResumetoDTO(resume);
    }

    /**
     * ---------------------- ADD NEW RESUME ----------------------
     * Purpose: Adds a new resume for a specific student.
     * Steps:
     *   1 Fetch the student
     *   2 Convert DTO → Entity
     *   3 Save resume
     *   4 Link to student
     *   5 Return response DTO
     */
    public ResumeResponseDTO addResume(Long studentId, ResumeRequestDTO resumeRequestDTO) {
        logger.info("Adding resume for student ID: {}", studentId);

        // 1 Fetch the student
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    return new RuntimeException("Student not found with ID: " + studentId);
                });

        // 2 Convert DTO → Entity
        Resume resume = convertDTOtoEntity(resumeRequestDTO);
        logger.debug("Converted ResumeRequestDTO to Resume entity: {}", resume.getResumeTitle());

        // 3 Save the resume
        Resume savedResume = resumeRepo.save(resume);
        logger.info("Resume '{}' saved successfully with ID: {}", savedResume.getResumeTitle(), savedResume.getId());

        // 4 Link resume to student
        student.setResume(savedResume);
        studentRepo.save(student);
        logger.info("Linked resume '{}' to student '{}'", savedResume.getResumeTitle(), student.getName());

        // 5 Return DTO
        return mapResumetoDTO(savedResume);
    }

    // ---------------------- HELPER METHODS ----------------------

    /**
     * Converts a Resume entity to a ResumeResponseDTO.
     * These are small helper methods — usually no need to log unless debugging.
     */
    private ResumeResponseDTO mapResumetoDTO(Resume resume) {
        ResumeResponseDTO dto = new ResumeResponseDTO();
        dto.setResumeTitle(resume.getResumeTitle());
        dto.setFileData(resume.getFileData());
        return dto;
    }

    /**
     * Converts a ResumeRequestDTO (from the controller) into a Resume entity.
     */
    private Resume convertDTOtoEntity(ResumeRequestDTO resumeRequestDTO) {
        Resume resume = new Resume();
        resume.setResumeTitle(resumeRequestDTO.getResumeTitle());
        resume.setFilePath(resumeRequestDTO.getFilePath());
        return resume;
    }


    public Resume storeFileAndAddResume(Long studentId, String title, MultipartFile file) {

        Student student=studentRepo.findById(studentId)
                .orElseThrow(()->new StudentNotFoundException("Student not found with the id"+studentId));
        String originalFileName= StringUtils.cleanPath(file.getOriginalFilename());
        File targetFile = new File(uploadDir + "/resumes/" + originalFileName);

        try {

            Files.createDirectories(targetFile.getParentFile().toPath());


            InputStream fileInputStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(fileInputStream,targetFile);

        } catch (IOException ex) {
            // This happens if the server doesn't have permission to write the file
            throw new RuntimeException("Could not store file " + originalFileName, ex);
        }

        Resume resume=new Resume();
        resume.setResumeTitle(title);
        resume.setFileName(originalFileName);
        resume.setFileType(file.getContentType());

        try {
            resume.setFileData(file.getBytes());
            resume.setFilePath(targetFile.getPath());
        } catch (IOException ex) {
            throw new RuntimeException(" Not able to read file data " + originalFileName, ex);
        }

        Resume savedResume=resumeRepo.save(resume);

        student.setResume(savedResume);
        studentRepo.save(student);

        return savedResume;
    }


    public Resume getResumeForAuthenticatedUser(String username){
        Users user=userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));

        Student student=studentRepo.findByUserId(user.getId())
                .orElseThrow(()->new StudentNotFoundException("User is not linked to any student"));

        if(student==null){
            throw new ResumeNotFoundException("No student profile is linked to this account");
        }

        Resume resume=student.getResume();

        if (resume == null || resume.getFileData() == null) {
            throw new ResumeNotFoundException("No resume file data found for this student.");
        }

        return resume;
    }

}
