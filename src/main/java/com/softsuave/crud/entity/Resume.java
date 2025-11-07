package com.softsuave.crud.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Memory Comment: This is the second Entity class in your project.
 *
 * @Entity tells Hibernate (our ORM) that this class maps directly
 * to a table in our database. By default, the table will be named 'resume'.
 *
 * This table will be linked to the 'student' table via the
 * 'resume_id' foreign key we defined in the 'Student' entity.
 */
@Entity
@Data
public class Resume {

    /**
     * Memory Comment: This field is the Primary Key for our 'resume' table.
     *
     * @Id marks this field as the primary key.
     * @GeneratedValue tells Hibernate how to generate the value for this ID.
     * - strategy = GenerationType.AUTO: We let Hibernate decide the best
     * strategy (like "auto-increment") based on our database (e.g., MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Memory: These are standard columns in the 'resume' table.
    private String resumeTitle;

    private String fileName;

    private String fileType;

    private String filePath;


    @Lob
    @Column(columnDefinition = "LONGBLOB") // Be explicit for MySQL
    private byte[] fileData;


}
