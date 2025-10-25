package za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Attendance;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.SchoolClass;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.model.Student;
import za.ac.tut.smartAttendanceRegister.SmartAttandanceRegister.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Mark a student as present for today.
     */
    public void markPresent(Student student, SchoolClass schoolClass) {
        LocalDate today = LocalDate.now();
        // Avoid duplicate attendance for the same student today
        if (!attendanceRepository.existsByStudentIdAndDate(student.getId(), today)) {
            Attendance attendance = new Attendance(
                    student,
                    schoolClass,
                    today,
                    Attendance.Status.PRESENT
            );
            attendanceRepository.save(attendance);
        }
    }

    /**
     * Mark a student as absent for today.
     */
    public void markAbsent(Student student, SchoolClass schoolClass) {
        LocalDate today = LocalDate.now();
        if (!attendanceRepository.existsByStudentIdAndDate(student.getId(), today)) {
            Attendance attendance = new Attendance(
                    student,
                    schoolClass,
                    today,
                    Attendance.Status.ABSENT
            );
            attendanceRepository.save(attendance);
        }
    }

    /**
     * Calculate attendance percentage for a student within a custom date range.
     */
    public double calculateAttendancePercentage(Student student, LocalDate start, LocalDate end) {
        List<Attendance> records = attendanceRepository.findByStudentAndDateBetween(student, start, end);

        if (records.isEmpty()) return 0.0;

        long presentCount = records.stream()
                .filter(record -> record.getStatus() == Attendance.Status.PRESENT)
                .count();

        return (presentCount * 100.0) / records.size();
    }

    /**
     * Calculate attendance percentage for the current month.
     */
    public double calculateMonthlyAttendancePercentage(Student student) {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();
        return calculateAttendancePercentage(student, start, end);
    }

    /**
     * Notify parents if monthly attendance < 30%.
     */
    public void notifyLowAttendance(Student student) {
        double percentage = calculateMonthlyAttendancePercentage(student);
        if (percentage < 30.0) {
            emailService.sendEmail(
                    student.getParentEmail(),
                    "Attendance Warning",
                    "Your child " + student.getName() +
                            " has attendance below 30% for this month (" + String.format("%.2f", percentage) + "%)."
            );
        }
    }
}
