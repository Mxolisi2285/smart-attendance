package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByEmail(String email);
}

