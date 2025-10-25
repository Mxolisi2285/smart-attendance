package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // ✅ This works: override findAll() with @EntityGraph
    @Override
    @EntityGraph(attributePaths = "attendanceRecords")
    List<Student> findAll();
}