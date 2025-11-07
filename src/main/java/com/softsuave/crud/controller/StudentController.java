package com.softsuave.crud.controller;

import com.softsuave.crud.dto.StudentRequestDTO;
import com.softsuave.crud.dto.StudentResponseDTO;
import com.softsuave.crud.service.StudentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    // Logger specific to this controller — logs go to controller.log (per your logback.xml config)
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService; // Injecting the service layer

    /**
     * ---------------------- GET ALL STUDENTS ----------------------
     * Endpoint: GET /students
     * Purpose: Fetch all student records.
     * Logging: Starts and ends the request for traceability.
     */
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        logger.info("GET /students - Request received");

        // Calls service layer to fetch all students from DB
        List<StudentResponseDTO> students = studentService.getAllStudents();

        logger.info("GET /students - Returning {} students", students.size());
        return ResponseEntity.ok(students); // Returns 200 OK
    }

    /**
     * ---------------------- GET STUDENT BY ID ----------------------
     * Endpoint: GET /students/{id}
     * Purpose: Fetch one student record by ID.
     * Logging: Includes student ID for easier debugging.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        logger.info("GET /students/{} - Request received", id);

        // Calls service layer to get the student by ID
        StudentResponseDTO student = studentService.getStudentById(id);

        logger.info("GET /students/{} - Returning student: {}", id, student.getName());
        return ResponseEntity.ok(student); // Returns 200 OK
    }

    /**
     * ---------------------- CREATE STUDENT ----------------------
     * Endpoint: POST /students
     * Purpose: Adds a new student record to the database.
     * Logging: Logs student name before and after creation for clarity.
     */
    @PostMapping("/add")
    public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody StudentRequestDTO studentRequestDTO) {
        logger.info("POST /students - Request to create student:  {}", studentRequestDTO.getName());

        // Calls service layer to save the student into DB
        StudentResponseDTO createdStudent = studentService.createStudent(studentRequestDTO);

        logger.info("POST /students - Student created successfully with name: {}", createdStudent.getName());
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED); // Returns 201 Created
    }

    /**
     * ---------------------- UPDATE STUDENT ----------------------
     * Endpoint: PUT /students/{id}
     * Purpose: Updates an existing student record.
     * Logging: Captures which ID is being updated.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @RequestBody StudentRequestDTO studentRequestDTO) {

        logger.info("PUT /students/{} - Request to update student", id);

        // Calls service layer to perform the update
        StudentResponseDTO updatedStudent = studentService.updateStudent(id, studentRequestDTO);

        logger.info("PUT /students/{} - Student updated successfully", id);
        return ResponseEntity.ok(updatedStudent); // Returns 200 OK
    }

    /**
     * ---------------------- DELETE STUDENT ----------------------
     * Endpoint: DELETE /students/{id}
     * Purpose: Deletes a student record by ID.
     * Logging: Tracks which ID was deleted.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        logger.info("DELETE /students/{} - Request received", id);

        // Calls service layer to delete the record
        studentService.deleteStudent(id);

        logger.info("DELETE /students/{} - Student deleted successfully", id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}
