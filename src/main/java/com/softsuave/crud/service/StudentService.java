package com.softsuave.crud.service;

import com.softsuave.crud.dto.StudentRequestDTO;
import com.softsuave.crud.dto.StudentResponseDTO;
import com.softsuave.crud.entity.Student;
import com.softsuave.crud.exception.StudentNotFoundException;
import com.softsuave.crud.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memory Comment: This is the Service layer for Student.
 * Its only job is to handle all the "business logic" related to Students.
 *
 * It acts as the middle-man between the 'StudentController' (which handles web requests)
 * and the 'StudentRepository' (which handles database access).
 *
 * @Service tells Spring to create a single instance (a "bean") of this class,
 * so we can inject it into our controller.
 */
@Service
public class StudentService {

    // Memory: @Autowired is how we get the StudentRepository bean from Spring.
    // This is "Dependency Injection". We are "injecting" the database-handling
    // functionality (the repository) into our service layer.
    @Autowired
    public StudentRepository studentrepo;

    private static final Logger logger=LoggerFactory.getLogger(StudentService.class);

    /**
     * Retrieves a list of all students from the database.
     * This method is called by the StudentController's GET /students endpoint.
     */
    public List<StudentResponseDTO> getAllStudents() {
        logger.info("getAllStudents() called — fetching all student records from the database");

        // We just pass the call directly to the repository.
        List<Student> students = studentrepo.findAll();
        if (students.isEmpty()) {
            logger.warn("No student records found in the database");
            return Collections.emptyList();
        }
        logger.info("Successfully fetched {} students from the database", students.size());
        logger.debug("Student list: {}", students);

        // And maps them to DTOs before returning
        return students.stream()
                .map(this::mapStudentToDTO) // Create a helper method
                .collect(Collectors.toList());
    }
    // Helper method to student to DTOs
    private StudentResponseDTO mapStudentToDTO(Student student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setName(student.getName());
        dto.setBranch(student.getBranch());
        dto.setPercentage(student.getPercentage());
        if (student.getResume() != null) {
            dto.setResumeTitle(student.getResume().getResumeTitle());
        }
        return dto;
    }

    /**
     * Retrieves a single student by their ID.
     * This method is called by the StudentController's GET /students/{id} endpoint.
     */
    public StudentResponseDTO getStudentById(Long id) {
        // findById returns an 'Optional<Student>' which is a container
        // that might or might not hold a Student.
        // .orElse(null) means: "If you found the student, return it.
        // Otherwise, just return ResourceNotFoundException
        logger.info("Fetching student with id: {}", id);
        Student student=studentrepo.findById(id).orElseThrow(()->{
            logger.error("Student not found with id: {}", id);
            return new StudentNotFoundException("Student not found with id: " + id);
        });
        return mapStudentToDTO(student);
    }

    /**
     * Saves a new student to the database.
     * This method is called by the StudentController's POST /students/add endpoint.
     */
    public StudentResponseDTO createStudent(StudentRequestDTO studentRequestDTO) {
        // The .save() method is smart: if the student has no ID,
        // it creates a new one (INSERT). If it has an ID, it updates the existing one (UPDATE).
        logger.info("Adding student to database");
        Student student=convertToEntity(studentRequestDTO);

        Student savedStudent=studentrepo.save(student);
        if (logger.isInfoEnabled()) {
            logger.info("Student saved successfully"+student);
        }
        return mapStudentToDTO(savedStudent);
    }
    //Helper method to convert studentRequestDTo to student Entity
    private Student convertToEntity(StudentRequestDTO studentRequestDTO){
        Student student=new Student();
        student.setName(studentRequestDTO.getName());
        student.setPercentage(studentRequestDTO.getPercentage());
        student.setBranch(studentRequestDTO.getBranch());
        student.setResume(studentRequestDTO.getResume());
        return student;
    }



    /**
     * Updates an existing student in the database.
     * This method is called by the StudentController's PUT /students/update/{id} endpoint.
     */
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO newData) {
        logger.info("updateStudent() called with id: {} and StudentRequestDTO: {}", id, newData);
        // First, we must find the existing student in the database.
        Student student=studentrepo.findById(id).orElseThrow(()->{
            logger.error("Student not found with id: {}", id);
            return new StudentNotFoundException("Student not found with id: " + id);
        });
        logger.debug("Existing student before update: {}", student);
        // We manually update the fields on the 'student' object we found
        // with the data from the 'newData' object.

        // Update fields
        student.setName(newData.getName());
        student.setBranch(newData.getBranch());
        student.setPercentage(newData.getPercentage());
        student.setResume(newData.getResume());


        // We call .save() on the *updated* 'student' object.
        // Because 'student' already has an ID, Hibernate knows to
         // perform an UPDATE, not an INSERT.
        Student updatedStudent = studentrepo.save(student);
        logger.info("Student with id: {} successfully updated", id);
        logger.debug("Updated student details: {}", updatedStudent);

            return mapStudentToDTO(updatedStudent);
        }
        // If no student was found with that ID, we return null.

    /**
     * Deletes a student from the database by their ID.
     * This method is called by the StudentController's DELETE /students/delete/{id} endpoint.
     */
    public String deleteStudent(Long id) {
        logger.info("Attempting to delete student with ID: {}", id);
        // We just tell the repository to delete the record with this ID.
        // This method doesn't return anything, so we return our own success message.
        try {
            studentrepo.deleteById(id);
            logger.info("Successfully deleted student with ID: {}", id);
            return "Deleted";
        }catch (StudentNotFoundException e){
            logger.warn("No student found with ID: {}. Nothing to delete.", id);
            return "Student not found";
        }catch (Exception e) {
            // Log an error if something unexpected happens
            logger.error("Error occurred while deleting student with ID: {}", id, e);
            throw e;
        }
    }

}
