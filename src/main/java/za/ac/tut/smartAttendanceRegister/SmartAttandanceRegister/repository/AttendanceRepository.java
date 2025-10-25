package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Attendance;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate start, LocalDate end);
    boolean existsByStudentIdAndDate(Long studentId, LocalDate date);
    List<Attendance> findTop10ByOrderByDateDesc();
    @Query("SELECT a FROM Attendance a WHERE a.date = :date AND a.status = :status")
    List<Attendance> findByDateAndStatus(@Param("date") LocalDate date, @Param("status") Attendance.Status status);

    // ✅ Add this for CSV download
    List<Attendance> findAllByOrderByDateAsc();
}
