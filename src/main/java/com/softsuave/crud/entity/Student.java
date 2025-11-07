package com.softsuave.crud.entity;

import com.softsuave.crud.dto.ResumeRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Memory Comment: This is an Entity class.
 *
 * @Entity tells Hibernate (our ORM) that this class maps directly
 * to a table in our database. By default, the table will be named 'student'.
 *
 * @Data is a Lombok annotation that is a shortcut for:
 * - @Getter (generates all getter methods)
 * - @Setter (generates all setter methods)
 * - @ToString (generates a useful toString method)
 * - @EqualsAndHashCode (generates equals and hashCode methods)
 *
 * @AllArgsConstructor generates a constructor that takes all fields as arguments.
 * @NoArgsConstructor generates an empty constructor. Hibernate needs this to
 * create objects when it retrieves them from the database.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    /**
     * Memory Comment: This field is the Primary Key for our 'student' table.
     *
     * @Id marks this field as the primary key.
     * @GeneratedValue tells Hibernate how to generate the value for this ID.
     * - strategy = GenerationType.AUTO: We let Hibernate decide the best
     * strategy (like "auto-increment") based on our database (e.g., MySQL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Memory: These are standard columns in the 'student' table.
    private String name;
    private String branch;
    private float percentage;

    /**
     * Memory Comment: This is a database relationship.
     * This field links a Student to one Resume.
     *
     * @OneToOne specifies that one 'Student' row is linked to exactly one 'Resume' row.
     * - cascade = CascadeType.ALL: This is a "convenience" setting. It means:
     * "If I save a Student, also save its attached Resume."
     * "If I delete a Student, also delete its attached Resume."
     *
     * @JoinColumn tells Hibernate which column in *this* table (the 'student' table)
     * holds the foreign key.
     * - name = "resume_id": Create a column in the 'student' table named 'resume_id'.
     * - referencedColumnName = "id": This 'resume_id' column will store the value
     * from the 'id' column of the 'resume' table.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    private Resume resume;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

}
