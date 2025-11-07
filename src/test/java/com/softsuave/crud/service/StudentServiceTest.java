package com.softsuave.crud.service;

import com.softsuave.crud.dto.StudentRequestDTO;
import com.softsuave.crud.dto.StudentResponseDTO;
import com.softsuave.crud.entity.Student;
import com.softsuave.crud.exception.StudentNotFoundException; // Assuming you have this
import com.softsuave.crud.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Memory Comment: This is a strict UNIT TEST for the StudentService.
 *
 * @ExtendWith(MockitoExtension.class) - Initializes all mocks for us.
 * @Mock - Creates a "fake" StudentRepository. We control its behavior.
 * @InjectMocks - Creates a *real* StudentService and injects our
 * fake repository into it.
 */
@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    // This is the ENTITY. It's what the REPOSITORY returns.
    private Student studentEntity;

    // This is the DTO. It's what the SERVICE returns.
    private StudentResponseDTO studentResponseDTO;

    /**
     * Memory Comment: Runs before each test.
     * We set up a sample ENTITY (for the repository) and a sample
     * DTO (for the service) to use in our tests.
     */
    @BeforeEach
    void setup() {
        // This is the fake data we'll get from the "database" (mock repository)
        studentEntity = new Student();
        studentEntity.setId(1L);
        studentEntity.setName("Chaithu");
        studentEntity.setBranch("CSE");
        studentEntity.setPercentage(80.5F);

        // This is the DTO we expect our service to convert the entity into
        studentResponseDTO = new StudentResponseDTO();
        studentResponseDTO.setName("Chaithu");
        studentResponseDTO.setBranch("CSE");
        studentResponseDTO.setPercentage(80.5F);
    }

    @Test
    void testGetAllStudents() {
        // 1. Arrange: Teach the mock repository what to return.
        //    The repository always returns ENTITIES.
        when(studentRepository.findAll()).thenReturn(List.of(studentEntity));

        // 2. Act: Call the service method, which should return DTOs.
        List<StudentResponseDTO> studentDTOs = studentService.getAllStudents();

        // 3. Assert: Check the DTOs that the service returned.
        assertNotNull(studentDTOs);
        assertEquals(1, studentDTOs.size());
        assertEquals("Chaithu", studentDTOs.get(0).getName());
        assertEquals(80.5F, studentDTOs.get(0).getPercentage());

        // 4. Verify: Make sure the findAll() method was called exactly once.
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testGetStudentById_Found() {
        // 1. Arrange: Teach the repository to return our sample ENTITY
        //    when findById(1L) is called.
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));

        // 2. Act: Call the service method, which should return a DTO.
        StudentResponseDTO foundDTO = studentService.getStudentById(1L);

        // 3. Assert: Check the DTO.
        assertNotNull(foundDTO);
        assertEquals("Chaithu", foundDTO.getName());

        // 4. Verify: Make sure findById(1L) was called.
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetStudentById_NotFound() {
        // 1. Arrange: Teach the repository to return an empty Optional.
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert:
        //    We assert that calling the service method *throws* the
        //    StudentNotFoundException.
        //    This is much better than returning null.
        assertThrows(StudentNotFoundException.class, () -> {
            studentService.getStudentById(99L);
        });

        // 4. Verify: Make sure findById(99L) was called.
        verify(studentRepository, times(1)).findById(99L);
    }

    @Test
    void testAddStudent() {
        // 1. Arrange:
        //    This is the DTO the client sends *to* the service.
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setName("New Student");
        requestDTO.setBranch("IT");
        requestDTO.setPercentage(90.0F);

        //    Teach the repository: "When you save *any* Student
        //    entity, just return it."
        //    We use 'thenAnswer' to return the same object that was passed in.
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act: Call the service with the Request DTO.
        StudentResponseDTO savedDTO = studentService.createStudent(requestDTO);

        // 3. Assert: Check the Response DTO that the service returned.
        assertNotNull(savedDTO);
        assertEquals("New Student", savedDTO.getName());
        assertEquals("IT", savedDTO.getBranch());

        // 4. Verify: Make sure save() was called.
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_Found() {
        // 1. Arrange:
        //    This is the DTO with the *new* data.
        StudentRequestDTO updateDTO = new StudentRequestDTO();
        updateDTO.setName("Cherry");
        updateDTO.setPercentage(99.9F);
        updateDTO.setBranch("MECH");

        //    Teach the repository to first *find* the existing student.
        when(studentRepository.findById(1L)).thenReturn(Optional.of(studentEntity));

        //    Teach the repository to *save* the updated student.
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act: Call the service to perform the update.
        StudentResponseDTO updatedDTO = studentService.updateStudent(1L, updateDTO);

        // 3. Assert: Check that the returned DTO has the updated info.
        assertNotNull(updatedDTO);
        assertEquals("Cherry", updatedDTO.getName());
        assertEquals(99.9F, updatedDTO.getPercentage());
        assertEquals("MECH", updatedDTO.getBranch());

        // 4. Verify: Check that findById and save were both called once.
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testUpdateStudent_NotFound() {
        // 1. Arrange:
        //    Create a DTO with update data.
        StudentRequestDTO updateDTO = new StudentRequestDTO();
        updateDTO.setName("Cherry");

        //    Teach the repository to *not* find the student.
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert:
        //    Check that the service throws an exception.
        assertThrows(StudentNotFoundException.class, () -> {
            studentService.updateStudent(99L, updateDTO);
        });

        // 4. Verify:
        //    Check that findById was called, but 'save' was *never* called.
        verify(studentRepository, times(1)).findById(99L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testDeleteStudent() {
        // 1. Arrange
        //    'deleteById' returns 'void', so we use 'doNothing()'.
        doNothing().when(studentRepository).deleteById(1L);

        // 2. Act
        String result = studentService.deleteStudent(1L);

        // 3. Assert
        assertEquals("Deleted", result); // Check for the hardcoded string.

        // 4. Verify
        verify(studentRepository, times(1)).deleteById(1L);
    }
}

